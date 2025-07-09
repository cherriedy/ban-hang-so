package com.optlab.banhangso.features.main.brand.view

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
import com.optlab.banhangso.features.main.brand.adapters.BrandSelectionAdapter
import com.optlab.banhangso.features.main.brand.models.BrandUiModel
import com.optlab.banhangso.features.main.brand.viewmodel.BrandSelectionViewModel
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy
import com.optlab.banhangso.internal.utilities.itemspacing.LinearSpacingStrategy.Direction
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import java.util.EnumSet

@AndroidEntryPoint
class BrandSelectionFragment : Fragment() {

  companion object {
    const val BRAND_SELECTION_TRACKER = "BRAND_SELECTION_TRACKER"
    const val BRAND_SELECTION_REQUEST = "BRAND_SELECTION_REQUEST"
    const val BRAND_SELECTION_RESULT = "BRAND_SELECTION_RESULT"
  }

  private var _binding: FragmentOptionSelectionBinding? = null
  private val binding: FragmentOptionSelectionBinding
    get() = _binding!!

  private val viewModel: BrandSelectionViewModel by viewModels()

  private var listAdapter: BrandSelectionAdapter = BrandSelectionAdapter()
  private var selectionTracker: SelectionTracker<String>? = null

  private lateinit var args: BrandSelectionFragmentArgs

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

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    args = BrandSelectionFragmentArgs.fromBundle(requireArguments())

    setupRecyclerView()
    setupSelectionTracker(savedInstanceState)
    observeViewModel()

    setSelectedBrand(args.brandId)
  }

  private fun setSelectedBrand(brandId: String?) {
    selectionTracker?.let { tracker ->
      tracker.clearSelection()
      brandId?.let { tracker.select(it) }
    }
  }

  private fun observeViewModel() {
    viewModel.brands
      .to(AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this)))
      .subscribe { brands -> listAdapter.submitData(lifecycle, brands) }
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

  private fun setupSelectionTracker(savedInstanceState: Bundle?) {
    selectionTracker =
      SelectionTracker.Builder(
          BRAND_SELECTION_TRACKER,
          binding.rvOptions,
          // Provide the adapter as the key provider
          BrandSelectionAdapter.BrandKeyProvider(listAdapter),
          // Provide the adapter as the details lookup
          BrandSelectionAdapter.BrandDetailsLookup(binding.rvOptions),
          // Use a stable storage strategy for selection state
          StorageStrategy.createStringStorage(),
        )
        .withSelectionPredicate(
          // Indicate that we want single selection mode
          SelectionPredicates.createSelectSingleAnything()
        )
        .withOnItemActivatedListener { item, _ ->
          // Handle item activation (tap) by selecting/deselecting the item
          item.selectionKey?.let { key ->
            selectionTracker?.let { tracker ->
              if (tracker.isSelected(key)) {
                tracker.deselect(key)
              } else {
                tracker.clearSelection()
                tracker.select(key)
              }
            }
          }
          true // Return true to indicate we handled the activation
        }
        .build()

    // Set the selection tracker on the adapter
    listAdapter.selectionTracker = selectionTracker

    // Restore previous selection state if available
    savedInstanceState?.let { selectionTracker?.onRestoreInstanceState(it) }

    // Observe selection changes and send result back to calling fragment
    selectionTracker?.addObserver(
      object : SelectionTracker.SelectionObserver<String>() {
        override fun onSelectionChanged() {
          super.onSelectionChanged()

          // Send back the selected brand when selection changes
          getSelectedBrand()?.let { brand ->
            bundleOf(BRAND_SELECTION_RESULT to brand).also {
              setFragmentResult(BRAND_SELECTION_REQUEST, it)
            }
          }
        }
      }
    )
  }

  /** Get the currently selected brand, or null if nothing is selected */
  private fun getSelectedBrand(): BrandUiModel? =
    selectionTracker?.selection?.singleOrNull().let { selectedId ->
      (0 until listAdapter.itemCount)
        .asSequence()
        .mapNotNull { listAdapter.getBrandAt(it) }
        .firstOrNull { it.id == selectedId }
    }
}
