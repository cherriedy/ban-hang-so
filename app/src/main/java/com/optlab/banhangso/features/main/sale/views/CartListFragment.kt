package com.optlab.banhangso.features.main.sale.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.optlab.banhangso.R
import com.optlab.banhangso.databinding.FragmentCartListBinding
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel
import com.optlab.banhangso.features.main.customer.views.CustomerSelectionFragment
import com.optlab.banhangso.features.main.sale.adapters.CartListAdapter
import com.optlab.banhangso.features.main.sale.listeners.CartItemListener
import com.optlab.banhangso.features.main.sale.models.CartUiModel
import com.optlab.banhangso.features.main.sale.viewmodels.SaleViewModel
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction.BOTTOM
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.EnumSet

@AndroidEntryPoint
class CartListFragment : Fragment() {
    private var _binding: FragmentCartListBinding? = null
    private val binding: FragmentCartListBinding
        get() = _binding!!

    private val viewModel: SaleViewModel by hiltNavGraphViewModels(R.id.sale_navigation)

    private lateinit var navController: NavController
    private lateinit var listAdapter: CartListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter =
            CartListAdapter(
                object : CartItemListener {
                    override fun onQuantityChanged(item: CartUiModel.Item) {
                        viewModel.updateQuantity(item)
                        Timber.i("Cart item quantity changed: ${item.name} -> ${item.quantity}")
                    }

                    override fun onItemRemoved(item: CartUiModel.Item) {
                        viewModel.removeItem(item)
                        Timber.i("Cart item removed: ${item.name}")
                    }
                },
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartListBinding.inflate(inflater, container, false)
        binding.apply {
            this.lifecycleOwner = viewLifecycleOwner
            this.fragment = this@CartListFragment
            this.viewModel = this@CartListFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        binding.mtb.setNavigationOnClickListener { navController.navigateUp() }
        setupRecyclerView()

        observerViewModel()
        registerCustomerSelectionListener()
    }

    private fun registerCustomerSelectionListener() {
        parentFragmentManager.setFragmentResultListener(
            CustomerSelectionFragment.CUSTOMER_SELECTION_REQUEST,
            viewLifecycleOwner,
        ) { _, result ->
            @Suppress("DEPRECATION")
            val customer =
                result.getSerializable(CustomerSelectionFragment.CUSTOMER_SELECTION_RESULT)
                    as CustomerUiModel
            viewModel.setCustomer(customer)
        }
    }

    private fun observerViewModel() {
        viewModel.cart.observe(viewLifecycleOwner) { cart ->
            Timber.i("Cart: $cart")
            listAdapter.submitList(cart.asList())
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    fun navigateToCustomerSelection(
        @Suppress("UNUSED_PARAMETER") view: View,
    ) {
        val customerId = viewModel.cart.value?.customer?.id.orEmpty()
        CartListFragmentDirections.actionToCustomerSelection(customerId).also {
            Timber.d("Navigating to customer selection with ID: $customerId")
            navController.navigate(it)
        }
    }

    fun navigateToPayment(
        @Suppress("UNUSED_PARAMETER") view: View,
    ) {
        CartListFragmentDirections.actionToPayment().also { navController.navigate(it) }
    }

    private fun setupRecyclerView() {
        setupProductItemSpacing()
        binding.rvItems.setHasFixedSize(false)
        binding.rvItems.adapter = listAdapter
    }

    private fun setupProductItemSpacing() {
        val linearSpacingStrategy = LinearSpacingStrategy(context, 8, EnumSet.of(BOTTOM))
        binding.rvItems.addItemDecoration(SpacingItemDecoration(linearSpacingStrategy))
    }
}
