package com.optlab.banhangso.ui.product.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;
import com.optlab.banhangso.ui.adapter.CategoryTagSelectionAdapter;
import com.optlab.banhangso.ui.adapter.ProductListAdapter;
import com.optlab.banhangso.databinding.FragmentProductListBinding;
import com.optlab.banhangso.ui.common.decoration.GridSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.SpacingStrategy;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.ui.common.decoration.SpacingItemDecoration;
import com.optlab.banhangso.util.UserPreferenceManager;
import com.optlab.banhangso.ui.product.viewmodel.ProductListViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabListViewModel;

import java.util.EnumSet;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductListFragment extends Fragment {
  @Inject protected CategoryRepository categoryRepository;
  private FragmentProductListBinding binding;
  private ProductListViewModel productListViewModel;
  private ProductSortSelectionViewModel productSortSelectionViewModel;
  private ProductTabListViewModel productTabListViewModel;
  private ProductListAdapter productListAdapter;
  private CategoryTagSelectionAdapter categoryTagSelectionAdapter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initViewModels();

    // Setup adapter for RecyclerView of products with callback to edit a specific product.
    productListAdapter =
        new ProductListAdapter(
            productId -> {
              NavDirections action = ProductTabsFragmentDirections.actionToEdit(productId, false);
              NavHostFragment.findNavController(this).navigate(action);
            });

    // Setup adapter for RecyclerView of categories with callback to select category. If the
    // position is NO_POSITION (-1), set the selection as null to trigger default sort. Otherwise,
    // the position of selected category is set to ProductListViewModel.
    categoryTagSelectionAdapter =
        new CategoryTagSelectionAdapter(
            position ->
                productListViewModel.setCategory(
                    position == RecyclerView.NO_POSITION
                        ? null
                        : categoryRepository.getCategoryByPosition(position)));

    // Get the previous sort option from user preference and set it to ProductListViewModel.
    UserPreferenceManager userPreferenceManager = new UserPreferenceManager(requireContext());
    productListViewModel.setSortOption(userPreferenceManager.getSortOption());
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProductListBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    setupProductRecyclerView();
    observeViewModels();

    setupCategoryRecyclerView();
  }

  @Override
  public void onDestroyView() {
    binding.recyclerViewProduct.setAdapter(null);
    binding = null;
    super.onDestroyView();
  }

  /**
   * Helper method used to retrieve a parent {@code ViewModel} via {@link NavBackStackEntry}
   *
   * @param viewModelClass the class of the {@code ViewModel}
   * @param <T> the type of the {@code ViewModel}
   * @return the parent {@code ViewModel}
   */
  private <T extends ViewModel> T getParentViewModel(Class<T> viewModelClass) {
    // NavController navController = NavHostFragment.findNavController(requireParentFragment());
    // NavBackStackEntry parentBackStackEntry =
    //     Objects.requireNonNull(
    //         navController.getPreviousBackStackEntry(), "Parent back stack entry is null.");
    // return new ViewModelProvider(parentBackStackEntry).get(viewModelClass);
    return new ViewModelProvider((requireParentFragment())).get(viewModelClass);
  }

  /**
   * Helper method used to retrieve a current {@code ViewModel} via {@link NavBackStackEntry}
   *
   * @param viewModelClass the class of the {@code ViewModel}
   * @param <T> the type of the {@code ViewModel}
   * @return the current {@code ViewModel}
   */
  private <T extends ViewModel> T getCurrentViewModel(Class<T> viewModelClass) {
    // NavController navController = NavHostFragment.findNavController(this);
    // NavBackStackEntry currentBackStackEntry =
    //     Objects.requireNonNull(
    //         navController.getCurrentBackStackEntry(), "Current back stack entry is null.");
    // return new ViewModelProvider(currentBackStackEntry).get(viewModelClass);
    return new ViewModelProvider(this).get(viewModelClass);
  }

  private void initViewModels() {
    productListViewModel = getParentViewModel(ProductListViewModel.class);
    productTabListViewModel = getParentViewModel(ProductTabListViewModel.class);
    productSortSelectionViewModel = getCurrentViewModel(ProductSortSelectionViewModel.class);
  }

  /** Set the adapter and optimization options for RecyclerView. */
  private void setupProductRecyclerView() {
    // Observe the sort option changes and set it to ProductListViewModel.
    productSortSelectionViewModel
        .getSortOption()
        .observe(getViewLifecycleOwner(), productListViewModel::setSortOption);

    // Observer layout toggles to update the RecyclerView's layout.
    productTabListViewModel
        .getToggleLayout()
        .observe(getViewLifecycleOwner(), this::setupRecyclerViewLayout);

    binding.recyclerViewProduct.setHasFixedSize(true);
    binding.recyclerViewProduct.setAdapter(productListAdapter);
  }

  private void observeViewModels() {
    // Observe the products and update the adapter.
    productListViewModel
        .getProducts()
        .observe(
            getViewLifecycleOwner(),
            products -> {
              productListAdapter.submitList(null);
              productListAdapter.submitList(products);
            });

    // Observe the categories and update the adapter.
    productListViewModel
        .getCategories()
        .observe(getViewLifecycleOwner(), categoryTagSelectionAdapter::submitList);
  }

  /**
   * Dynamically sets up the RecyclerView layout as grid or linear based on a boolean flag.
   *
   * @param isGrid the boolean flag to determine the layout
   */
  private void setupRecyclerViewLayout(Boolean isGrid) {
    Context context = requireContext();
    // Remove the existing decorations.
    while (binding.recyclerViewProduct.getItemDecorationCount() > 0) {
      binding.recyclerViewProduct.removeItemDecorationAt(0);
    }

    if (isGrid) {
      GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
      SpacingStrategy spacingStrategy = new GridSpacingStrategy(context, 8);
      binding.recyclerViewProduct.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
      binding.recyclerViewProduct.setLayoutManager(gridLayoutManager);
      productListAdapter.setItemLayoutRes(R.layout.grid_item_product);
    } else {
      LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
      SpacingStrategy spacingStrategy = new LinearSpacingStrategy(context, 8);
      binding.recyclerViewProduct.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
      binding.recyclerViewProduct.setLayoutManager(linearLayoutManager);
      productListAdapter.setItemLayoutRes(R.layout.list_item_horizontal_product);
    }
  }

  /** Sets up the RecyclerView for displaying category tags. */
  private void setupCategoryRecyclerView() {
    EnumSet<LinearSpacingStrategy.Direction> directions =
        EnumSet.of(LinearSpacingStrategy.Direction.LEFT, LinearSpacingStrategy.Direction.RIGHT);
    SpacingStrategy spacingStrategy = new LinearSpacingStrategy(requireContext(), 8, directions);
    binding.rvCategory.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
    binding.rvCategory.setAdapter(categoryTagSelectionAdapter);

    Category selectedCategory = productListViewModel.getSelectedCategory().getValue();
    if (selectedCategory != null) {
      int pos = categoryRepository.getPositionById(selectedCategory.getId());
      categoryTagSelectionAdapter.setSelectedPosition(pos);
    }
  }
}
