package com.optlab.banhangso.ui.category.viewmodel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.ui.adapter.CategorySelectionAdapter;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;

import java.util.Collections;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

/**
 * Fragment that displays a selectable list of categories with search functionality. This fragment
 * allows users to select a category and passes the selection back to the previous fragment through
 * the Navigation component's SavedStateHandle.
 */
@HiltViewModel
public class CategorySelectionFragment extends Fragment {
  /** Key used for storing the selected category position in SavedStateHandle */
  public static final String KEY_CHECKED_POSITION = "checked_category_position_key";

  @Inject protected CategoryRepositoryImpl repository;

  private FragmentOptionSelectionBinding binding;
  private CategorySelectionViewModel viewModel;
  private CategorySelectionAdapter adapter;

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentOptionSelectionBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    viewModel = new ViewModelProvider(this).get(CategorySelectionViewModel.class);
    setupCategoryListAdapter();
    handleSearchView();
  }

  /**
   * This method is called when an item in the adapter is selected. It retrieves the checked
   * position and sets it in the previous back stack entry's SavedStateHandle.
   *
   * @param checkedPosition The position of the selected item in the RecyclerView.
   */
  private void onItemSelected(int checkedPosition) {
    if (checkedPosition != RecyclerView.NO_POSITION) {
      NavController navController = NavHostFragment.findNavController(this);
      NavBackStackEntry prevBackStackEntry = navController.getPreviousBackStackEntry();

      if (prevBackStackEntry != null) {
        prevBackStackEntry
            .getSavedStateHandle() // Get the previous back stack entry's SavedStateHandle
            .set(KEY_CHECKED_POSITION, checkedPosition); // Set back the checked position
      } else {
        Timber.e("Previous back stack entry is null.");
      }
    }
  }

  /**
   * Sets up the RecyclerView adapter for displaying categories. Retrieves the category ID from
   * arguments, initializes the adapter with the correct selection, and observes the category list
   * from the ViewModel.
   */
  private void setupCategoryListAdapter() {
    // Get the arguments passed to this fragment, specifically the category ID.
    CategorySelectionFragmentArgs args =
        CategorySelectionFragmentArgs.fromBundle(requireArguments());

    // Initialize the RecyclerView adapter with a listener for item selection.
    adapter = new CategorySelectionAdapter(this::onItemSelected);

    // Set the initial checked position in the adapter based on the category ID passed.
    adapter.setCheckedPosition(repository.getPositionById(args.getCategoryId()));

    binding.listOption.setAdapter(adapter);
    binding.listOption.setHasFixedSize(true);

    // Observe the checked position in the ViewModel and update the adapter accordingly.
    viewModel
        .getCategoriesSourceLiveData()
        .observe(
            getViewLifecycleOwner(),
            categories -> {
              if (categories == null || categories.isEmpty()) {
                adapter.submitList(Collections.emptyList());
                Timber.e("Categories list is empty or null.");
              } else {
                adapter.submitList(categories);
              }
            });
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    binding = null;
  }

  /** Update the search query in the ViewModel when text changes. */
  private void handleSearchView() {
    binding.svKeyword.setOnQueryTextListener(
        new SearchView.OnQueryTextListener() {
          @Override
          public boolean onQueryTextSubmit(String query) {
            return false;
          }

          @Override
          public boolean onQueryTextChange(String newText) {
            viewModel.setSearchQuery(newText); // Update the search query in the ViewModel
            return true;
          }
        });
  }
}
