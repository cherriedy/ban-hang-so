package com.optlab.banhangso.ui.product.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.ui.adapter.ProductSortSelectionAdapter;
import com.optlab.banhangso.databinding.FragmentProductSortSelectionBinding;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.impl.ProductSortOptionRepositoryImpl;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.util.UserPreferenceManager;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Display a selectable list of product sort options.
 *
 * <p>Fragment that displays a selectable list of product sort options. This fragment allows users
 * to select a sort option and passes the selection back to the previous fragment through the
 * Navigation component's SavedStateHandle.
 */
@AndroidEntryPoint
public class ProductSortSelectionFragment extends BottomSheetDialogFragment {
  @Inject protected ProductSortOptionRepository repository;
  private FragmentProductSortSelectionBinding binding;
  private ProductSortSelectionViewModel viewModel;
  private ProductSortSelectionAdapter adapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setupNavigation();
    setupRecyclerView();
  }

  private void setupNavigation() {
    // NavController navController = NavHostFragment.findNavController(this);
    // // Get the previous back stack entry to retrieve the ViewModel.
    // NavBackStackEntry prevBackStackEntry = navController.getPreviousBackStackEntry();
    // if (prevBackStackEntry != null) {
    //   viewModel =
    //       new ViewModelProvider(prevBackStackEntry).get(ProductSortSelectionViewModel.class);
    //   new ViewModelProvider(prevBackStackEntry).get(ProductSortSelectionViewModel.class);
    // } else {
    //   Timber.e("Previous back stack entry is null. Cannot retrieve ViewModel.");
    // }

    viewModel =
            new ViewModelProvider(requireParentFragment()).get(ProductSortSelectionViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProductSortSelectionBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.rvSortSelection.setHasFixedSize(true);
    binding.rvSortSelection.setAdapter(adapter);
  }

  /**
   * Sets up the RecyclerView with a list of sort options.
   *
   * <p>Sets up the RecyclerView with a list of sort options. It retrieves the saved sort option
   * from UserPreferenceManager and the sort option list from the ViewModel. The selected sort
   * option is saved in UserPreferenceManager and set in the ViewModel when a sort option is
   * clicked.
   */
  private void setupRecyclerView() {
    // Retrieve the saved sort option from UserPreferenceManager.
    UserPreferenceManager userPreferenceManager = new UserPreferenceManager(requireContext());
    SortOption<Product.SortField> savedSortOption = userPreferenceManager.getSortOption();

    // Retrieve the sort option list from ViewModel, throwing an exception whether it is null.
    List<SortOption<Product.SortField>> sortOptions = viewModel.getSortOptions();

    adapter =
        new ProductSortSelectionAdapter(
            sortOptions, // Submit the list of sort options to the adapter.
            sortOption -> { // Set up the click listener for each sort option.
              userPreferenceManager.saveSortOption(sortOption); // Save the selected sort option.
              viewModel.setSortOption(sortOption); // Set the selected sort option in ViewModel.
            });

    // Set the checked position in the adapter based on the saved sort option.
    adapter.setCheckedPosition(repository.getPosition(savedSortOption));
  }
}
