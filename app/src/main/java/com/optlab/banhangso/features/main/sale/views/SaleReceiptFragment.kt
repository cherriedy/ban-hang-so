package com.optlab.banhangso.features.main.sale.views

import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.findNavController
import com.optlab.banhangso.NavGraphDirections
import com.optlab.banhangso.R
import com.optlab.banhangso.databinding.FragmentSaleReceiptBinding
import com.optlab.banhangso.features.main.sale.adapters.SaleReceiptAdapter
import com.optlab.banhangso.features.main.sale.viewmodels.SaleViewModel
import com.optlab.banhangso.internal.utilities.NavigationUtils
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import java.util.EnumSet

class SaleReceiptFragment : Fragment() {
    private var _binding: FragmentSaleReceiptBinding? = null
    private val binding: FragmentSaleReceiptBinding
        get() = _binding!!

    private val viewModel: SaleViewModel by hiltNavGraphViewModels(R.id.sale_navigation)
    private val listAdapter: SaleReceiptAdapter = SaleReceiptAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSaleReceiptBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            fragment = this@SaleReceiptFragment
            viewModel = this@SaleReceiptFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupScrollableNote()
    }

    fun onExit(view: View) {
        val action = NavGraphDirections.actionToHomeGlobal()
        val option = NavigationUtils.getNavOptions(R.id.sale_navigation, true)
        view.findNavController().navigate(action, option)
    }

    private fun setupRecyclerView() {
        binding.rvItems.apply {
            addItemDecoration(
                SpacingItemDecoration(
                    LinearSpacingStrategy(context, 8, EnumSet.allOf(Direction::class.java)),
                ),
            )
            this.adapter = listAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.receipt.observe(viewLifecycleOwner) { listAdapter.submitList(it.items) }
    }

    private fun setupScrollableNote() {
        binding.tvNote.movementMethod = ScrollingMovementMethod.getInstance()
    }
}
