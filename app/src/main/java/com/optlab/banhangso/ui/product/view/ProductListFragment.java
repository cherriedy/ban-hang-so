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
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.databinding.FragmentProductListBinding;
import com.optlab.banhangso.ui.adapter.CategoryTagSelectionAdapter;
import com.optlab.banhangso.ui.adapter.ProductListAdapter;
import com.optlab.banhangso.ui.common.decoration.GridSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.SpacingItemDecoration;
import com.optlab.banhangso.ui.common.decoration.SpacingStrategy;
import com.optlab.banhangso.ui.product.viewmodel.ProductListViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.EnumSet;

import javax.inject.Inject;

@AndroidEntryPoint
public class ProductListFragment extends Fragment {
    @Inject protected CategoryRepository categoryRepository;

    private FragmentProductListBinding binding;
    private ProductListViewModel productListViewModel;
    private ProductTabHostSharedViewModel tabHostSharedViewModel;
    private ProductListAdapter productListAdapter;
    private CategoryTagSelectionAdapter categoryTagSelectionAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initAdapters();
    }

    private void initAdapters() {
        // Setup adapter for RecyclerView of products with callback to edit a specific product.
        productListAdapter = new ProductListAdapter(this::navigateToEditFragment);

        // Setup adapter for RecyclerView of category tags with callback to set the selected
        // category.
        categoryTagSelectionAdapter =
                new CategoryTagSelectionAdapter(productListViewModel::setCategory);
    }

    /** Navigates to the EditFragment when a product is clicked. */
    private void navigateToEditFragment(String productId) {
        NavDirections action = ProductListFragmentDirections.actionToProductEdit(productId, false);
        NavHostFragment.findNavController(this).navigate(action);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerViews();
        observeViewModels();
    }

    private void setupRecyclerViews() {
        setupProductRecyclerView();
        setupCategoryRecyclerView();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private <T extends ViewModel> T getParentViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(requireParentFragment()).get(viewModelClass);
    }

    private <T extends ViewModel> T getCurrentViewModel(Class<T> viewModelClass) {
        return new ViewModelProvider(this).get(viewModelClass);
    }

    private void initViewModels() {
        productListViewModel = getParentViewModel(ProductListViewModel.class);

        NavBackStackEntry productTabsEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        tabHostSharedViewModel =
                new ViewModelProvider(productTabsEntry).get(ProductTabHostSharedViewModel.class);
    }

    /** Sets up the RecyclerView for displaying products. */
    private void setupProductRecyclerView() {
        binding.recyclerViewProduct.setAdapter(productListAdapter);

        binding.recyclerViewProduct.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Set the item layout resource for the adapter based on the layout type.
        addSpacingDecoration(
                binding.recyclerViewProduct,
                new LinearSpacingStrategy(
                        requireContext(), 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class)));
    }

    private void observeViewModels() {
        // Observe the search query and update the adapter.
        productListViewModel
                .getProducts()
                .observe(
                        getViewLifecycleOwner(),
                        products -> {
                            // Bug: The product is not updated unless the list is set to null first.
                            productListAdapter.submitList(null);
                            productListAdapter.submitList(products);
                        });

        // Observe the categories and update the adapter.
        productListViewModel
                .getCategories()
                .observe(getViewLifecycleOwner(), categoryTagSelectionAdapter::submitList);

        // Observe the selected category and update the adapter.
        tabHostSharedViewModel
                .getGridModeEnabled()
                .observe(getViewLifecycleOwner(), this::updateRecyclerViewLayout);

        // Observe the search query and update the ViewModel.
        tabHostSharedViewModel
                .getSearchQuery()
                .observe(getViewLifecycleOwner(), productListViewModel::setSearchQuery);

        // Observe the selected sort option and update the ViewModel.
        tabHostSharedViewModel
                .getSelectedSortOption()
                .observe(getViewLifecycleOwner(), productListViewModel::setSortOption);
    }

    /**
     * Dynamically sets up the RecyclerView layout as grid or linear based on a boolean flag.
     *
     * @param isGrid the boolean flag to determine the layout
     */
    private void updateRecyclerViewLayout(Boolean isGrid) {
        Context context = requireContext();

        // Set the item layout resource for the adapter based on the layout type.
        productListAdapter.setItemLayoutRes(
                isGrid ? R.layout.grid_item_product : R.layout.list_item_horizontal_product);

        // Set the layout manager for the RecyclerView based on the layout type.
        binding.recyclerViewProduct.setLayoutManager(
                isGrid ? new GridLayoutManager(context, 2) : new LinearLayoutManager(context));

        // Add spacing decoration to the RecyclerView based on the layout type.
        addSpacingDecoration(
                binding.recyclerViewProduct,
                isGrid
                        ? new GridSpacingStrategy(context, 8)
                        : new LinearSpacingStrategy(
                                context, 8, EnumSet.allOf(LinearSpacingStrategy.Direction.class)));
    }

    /** Sets up the RecyclerView for displaying category tags. */
    private void setupCategoryRecyclerView() {
        binding.rvCategory.setAdapter(categoryTagSelectionAdapter);

        addSpacingDecoration(
                binding.rvCategory,
                new LinearSpacingStrategy(
                        requireContext(),
                        8,
                        EnumSet.of(
                                LinearSpacingStrategy.Direction.LEFT,
                                LinearSpacingStrategy.Direction.RIGHT)));

        Category selectedCategory = productListViewModel.getSelectedCategory().getValue();
        if (selectedCategory != null) {
            int pos = categoryRepository.getPositionById(selectedCategory.getId());
            categoryTagSelectionAdapter.setSelectedPosition(pos);
        }
    }

    /**
     * Adds spacing decoration to the RecyclerView.
     *
     * @param view the RecyclerView to add the decoration to
     * @param spacingStrategy the spacing strategy to use for the decoration
     */
    private void addSpacingDecoration(RecyclerView view, SpacingStrategy spacingStrategy) {
        while (view.getItemDecorationCount() > 0) {
            view.removeItemDecorationAt(0);
        }
        view.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
    }

    /** Called when the user clicks the "Create" button to navigate to the EditFragment. */
    public void onCreateButtonClick(@NonNull View view) {
        NavDirections action = ProductListFragmentDirections.actionToProductEdit("", true);
        Navigation.findNavController(view).navigate(action);
    }
}
