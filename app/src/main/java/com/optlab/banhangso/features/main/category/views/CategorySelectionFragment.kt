package com.optlab.banhangso.features.main.category.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import autodispose2.AutoDispose
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding
import com.optlab.banhangso.features.main.category.adapters.CategorySelectionAdapter
import com.optlab.banhangso.features.main.category.models.CategoryUiModel
import com.optlab.banhangso.features.main.category.viewmodel.CategorySelectionViewModel
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.util.EnumSet

@AndroidEntryPoint
class CategorySelectionFragment : Fragment() {
    companion object {
        const val CATEGORY_SELECTION_TRACKER = "CATEGORY_SELECTION_TRACKER"
        const val CATEGORY_SELECTION_REQUEST = "CATEGORY_SELECTION_REQUEST"
        const val CATEGORY_SELECTION_RESULT = "CATEGORY_SELECTION_RESULT"
    }

    private var _binding: FragmentOptionSelectionBinding? = null
    private val binding: FragmentOptionSelectionBinding
        get() = _binding!!

    private val listAdapter: CategorySelectionAdapter = CategorySelectionAdapter()
    private val viewModel: CategorySelectionViewModel by viewModels()

    private var selectionTracker: SelectionTracker<String>? = null
    private lateinit var args: CategorySelectionFragmentArgs

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOptionSelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        selectionTracker?.onSaveInstanceState(outState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        args = CategorySelectionFragmentArgs.fromBundle(requireArguments())

        setupRecyclerView()
        setupSelectionTracker(savedInstanceState)
        observeViewModel()

        setSelectedCategory(args.categoryId)
    }

    private fun observeViewModel() {
        viewModel.categories
            .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
            .subscribe { categories -> listAdapter.submitData(lifecycle, categories) }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setSelectedCategory(categoryId: String?) {
        selectionTracker?.let { tracker ->
            // Clear any existing selection, then select the new category
            tracker.clearSelection()
            // Since SelectionTracker uses unique keys, we select by the category ID
            categoryId?.let { tracker.select(it) }
        }
    }

    private fun setupSelectionTracker(savedInstanceState: Bundle?) {
        selectionTracker =
            SelectionTracker.Builder(
                CATEGORY_SELECTION_TRACKER, // Unique ID for the tracker
                binding.rvOptions, // RecyclerView to track selections
                // Provide the key provider to get the selection keys
                CategorySelectionAdapter.CategoryKeyProvider(listAdapter),
                // Provide the details lookup to get the details of the selected items
                CategorySelectionAdapter.CategoryDetailsLookup(binding.rvOptions),
                // Provide the storage strategy for selection keys, using String keys
                StorageStrategy.createStringStorage(),
            )
                .withSelectionPredicate(
                    // Allow selection of a single item at a time
                    SelectionPredicates.createSelectSingleAnything(),
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
                    true // Return true to indicate the item was activated
                }
                .build()

        listAdapter.selectionTracker = selectionTracker

        savedInstanceState?.let { selectionTracker?.onRestoreInstanceState(it) }

        selectionTracker?.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()

                    // Send back the selected category when selection changes
                    getSelectedCategory()?.let { category ->
                        bundleOf(CATEGORY_SELECTION_RESULT to category).also {
                            setFragmentResult(CATEGORY_SELECTION_REQUEST, it)
                        }
                    }
                }
            },
        )
    }

    private fun setupRecyclerView() =
        with(binding.rvOptions) {
            addItemDecoration(
                SpacingItemDecoration(
                    LinearSpacingStrategy(context, 8, EnumSet.allOf(Direction::class.java)),
                ),
            )
            layoutManager = LinearLayoutManager(context, VERTICAL, false)
            adapter = listAdapter
        }

    private fun getSelectedCategory(): CategoryUiModel? =
        selectionTracker?.selection?.singleOrNull().let { selectedId ->
            (0 until listAdapter.itemCount)
                .asSequence()
                .mapNotNull { listAdapter.getCategoryAt(it) }
                .firstOrNull { it.id == selectedId }
        }
}
