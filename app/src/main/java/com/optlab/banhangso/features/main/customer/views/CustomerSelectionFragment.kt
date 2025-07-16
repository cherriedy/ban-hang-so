package com.optlab.banhangso.features.main.customer.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding
import com.optlab.banhangso.features.main.customer.adapters.CustomerSelectionAdapter
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel
import com.optlab.banhangso.features.main.customer.viewmodels.CustomerSelectionViewModel
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.util.EnumSet

@AndroidEntryPoint
class CustomerSelectionFragment : Fragment() {

    companion object {
        const val CUSTOMER_SELECTION_TRACKER = "CUSTOMER_SELECTION_TRACKER"
        const val CUSTOMER_SELECTION_REQUEST = "CUSTOMER_SELECTION_REQUEST"
        const val CUSTOMER_SELECTION_RESULT = "CUSTOMER_SELECTION_RESULT"
    }

    private var _binding: FragmentOptionSelectionBinding? = null
    private val binding: FragmentOptionSelectionBinding
        get() = _binding!!

    private val listAdapter: CustomerSelectionAdapter = CustomerSelectionAdapter()
    private val viewModel: CustomerSelectionViewModel by viewModels()

    private var selectionTracker: SelectionTracker<String>? = null

    private lateinit var args: CustomerSelectionFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOptionSelectionBinding.inflate(inflater, container, false)
        binding.apply { this.lifecycleOwner = viewLifecycleOwner }
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectionTracker?.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = CustomerSelectionFragmentArgs.fromBundle(requireArguments())

        setupRecyclerView()
        setupSelectionTracker(savedInstanceState)
        setSelectedCustomer(args.customerId)

        observerViewModel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setSelectedCustomer(customerId: String?) {
        selectionTracker?.let { tracker ->
            tracker.clearSelection()
            customerId?.let { tracker.select(it) }
        }
    }

    private fun observerViewModel() {
        viewModel.customers
            .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { customers -> listAdapter.submitData(lifecycle, customers) }
    }

    private fun setupSelectionTracker(savedInstanceState: Bundle?) {
        selectionTracker =
            SelectionTracker.Builder(
                    CUSTOMER_SELECTION_TRACKER,
                    binding.rvOptions,
                    CustomerSelectionAdapter.CustomerKeyProvider(listAdapter),
                    CustomerSelectionAdapter.CustomerDetailsLookup(binding.rvOptions),
                    StorageStrategy.createStringStorage(),
                )
                .withSelectionPredicate(
                    // Allow selection of a single item at a time
                    SelectionPredicates.createSelectSingleAnything()
                )
                .withOnItemActivatedListener { item, _ ->
                    item.selectionKey?.let { key ->
                        selectionTracker?.let { tracker ->
                            if (!tracker.isSelected(key)) {
                                tracker.clearSelection()
                                tracker.select(key)
                            }
                        }
                    }
                    true
                }
                .build()

        listAdapter.selectionTracker = selectionTracker

        savedInstanceState?.let { selectionTracker?.onRestoreInstanceState(it) }

        selectionTracker?.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    getSelectedCustomer()?.let { customer ->
                        bundleOf(CUSTOMER_SELECTION_RESULT to customer).also {
                            parentFragmentManager.setFragmentResult(CUSTOMER_SELECTION_REQUEST, it)
                        }
                    }
                }
            }
        )
    }

    private fun setupRecyclerView() =
        with(binding.rvOptions) {
            addItemDecoration(
                SpacingItemDecoration(
                    LinearSpacingStrategy(context, 8, EnumSet.allOf(Direction::class.java))
                )
            )
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = listAdapter
        }

    private fun getSelectedCustomer(): CustomerUiModel? =
        selectionTracker?.selection?.singleOrNull().let { selectedId ->
            (0 until listAdapter.itemCount)
                .asSequence()
                .mapNotNull { listAdapter.getCustomerAt(it) }
                .firstOrNull { it.id == selectedId }
        }
}
