package com.optlab.banhangso.features.main.sale.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.optlab.banhangso.R
import com.optlab.banhangso.databinding.FragmentPaymentBinding
import com.optlab.banhangso.features.main.sale.adapters.PaymentListAdapter
import com.optlab.banhangso.features.main.sale.viewmodels.SaleViewModel
import com.optlab.banhangso.features.shared.views.LoadingDialog
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration

class PaymentFragment : Fragment() {

    private var _binding: FragmentPaymentBinding? = null
    private val binding: FragmentPaymentBinding
        get() = _binding!!

    private val viewModel: SaleViewModel by hiltNavGraphViewModels(R.id.sale_navigation)
    private val loadingDialog: LoadingDialog = LoadingDialog()

    private lateinit var listAdapter: PaymentListAdapter
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listAdapter =
            PaymentListAdapter { paymentType ->
                viewModel.setPaymentMethod(paymentType)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@PaymentFragment.viewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = NavHostFragment.findNavController(this)
        binding.mtb.setNavigationOnClickListener { navController.navigateUp() }
        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner, this::handleLoadingState)
        viewModel.messageResId.observe(viewLifecycleOwner, this::showToast)
        viewModel.paymentResult.observe(viewLifecycleOwner, this::navigateToReceipt)
    }

    private fun navigateToReceipt(result: Boolean) {
        if (result) {
            PaymentFragmentDirections.actionToReceipt().let { navController.navigate(it) }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(context, getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun handleLoadingState(isLoading: Boolean) {
        if (isLoading) {
            loadingDialog.show(parentFragmentManager, "loadingDialog_" + this.javaClass.simpleName)
        } else if (loadingDialog.isAdded) {
            loadingDialog.dismissAllowingStateLoss()
        }
    }

    private fun setupRecyclerView() {
        binding.rvMethods.apply {
            addItemDecoration(SpacingItemDecoration(GridSpacingStrategy(context, 16)))
            this.adapter = listAdapter
        }
    }
}
