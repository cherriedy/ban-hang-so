package com.optlab.banhangso.features.main.sale.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.fragment.NavHostFragment
import androidx.paging.LoadState
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.optlab.banhangso.R
import com.optlab.banhangso.databinding.FragmentSaleListBinding
import com.optlab.banhangso.features.main.sale.adapters.SaleListAdapter
import com.optlab.banhangso.features.main.sale.listeners.CartItemListener
import com.optlab.banhangso.features.main.sale.models.CartUiModel
import com.optlab.banhangso.features.main.sale.viewmodels.SaleViewModel
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.ConnectException

@AndroidEntryPoint
class SaleListFragment : Fragment() {

    private var _binding: FragmentSaleListBinding? = null
    private val binding: FragmentSaleListBinding
        get() = _binding!!

    private val viewModel: SaleViewModel by hiltNavGraphViewModels(R.id.sale_navigation)

    private lateinit var listAdapter: SaleListAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter =
            SaleListAdapter(
                object : CartItemListener {
                    override fun onQuantityChanged(item: CartUiModel.Item) {
                        viewModel.updateQuantity(item)
                        Timber.i("Product quantity changed: ${item.name} -> ${item.quantity}")
                    }

                    override fun onItemRemoved(item: CartUiModel.Item) {
                        viewModel.removeItem(item)
                        Timber.i("Product removed from cart: ${item.name}")
                    }
                },
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSaleListBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SaleListFragment
            viewModel = this@SaleListFragment.viewModel
            bsPrices.setViewModel(this@SaleListFragment.viewModel)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        binding.mtb.setNavigationOnClickListener { navController.navigateUp() }
        setupRecyclerView()
        observeViewModel()
        observePagingError()
    }

    private fun observePagingError() {
        lifecycleScope.launch {
            listAdapter.loadStateFlow.collect { loadStates ->
                val error =
                    listOf(
                        loadStates.source.refresh,
                        loadStates.source.append,
                        loadStates.source.prepend,
                    )
                        .firstOrNull { it is LoadState.Error } as? LoadState.Error
                error?.let {
                    val messageResId =
                        if (it.error is ConnectException) {
                            R.string.error_network
                        } else {
                            R.string.error_unknown
                        }
                    Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.items
            .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { items -> listAdapter.submitData(lifecycle, items) }
    }

    private fun setupRecyclerView() {
        setupProductSaleItemSpacing()
        setupProductSaleRefreshState()
        binding.rvProducts.setHasFixedSize(true)
        binding.rvProducts.adapter = listAdapter
    }

    private fun setupProductSaleRefreshState() {
        binding.srlProductSales.setOnRefreshListener { listAdapter.refresh() }
        listAdapter.addLoadStateListener { loadStates ->
            val isLoading = loadStates.refresh is LoadState.Loading
            binding.srlProductSales.isRefreshing = isLoading
        }
    }

    private fun setupProductSaleItemSpacing() {
        val gridSpacingStrategy = GridSpacingStrategy(requireContext(), 8)
        binding.rvProducts.addItemDecoration(SpacingItemDecoration(gridSpacingStrategy))
    }

    fun displayPrices(@Suppress("UNUSED_PARAMETER") view: View) {
        val isVisible = binding.bsPrices.clPrices.isVisible
        binding.bsPrices.clPrices.visibility = if (isVisible) View.GONE else View.VISIBLE
        binding.vOverlay.visibility = if (isVisible) View.GONE else View.VISIBLE
    }

    fun navigateToCart(@Suppress("UNUSED_PARAMETER") view: View) {
        val action: NavDirections = SaleListFragmentDirections.actionToCart()
        navController.navigate(action)
    }
}
