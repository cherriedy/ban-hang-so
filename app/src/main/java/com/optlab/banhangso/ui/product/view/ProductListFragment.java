package com.optlab.banhangso.ui.product.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.ui.adapter.CategoryTagSelectionAdapter;
import com.optlab.banhangso.ui.adapter.ProductListAdapter;
import com.optlab.banhangso.databinding.FragmentProductListBinding;
import com.optlab.banhangso.ui.common.decoration.GridSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.SpacingStrategy;
import com.optlab.banhangso.factory.ProductListViewModelFactory;
import com.optlab.banhangso.factory.ProductSortSelectionViewModelFactory;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.ui.common.decoration.SpacingItemDecoration;
import com.optlab.banhangso.util.UserPreferenceManager;
import com.optlab.banhangso.ui.product.viewmodel.ProductListViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabListViewModel;

import java.util.EnumSet;
import java.util.Objects;

public class ProductListFragment extends Fragment {

    private FragmentProductListBinding binding;
    private ProductListViewModel viewModel;
    private ProductListAdapter adapter;
    private ProductSortSelectionViewModel sortSelectionViewModel;
    private ProductTabListViewModel productTabListViewModel;
    private CategoryRepository categoryRepository;
    private CategoryTagSelectionAdapter categoryAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        categoryRepository = CategoryRepository.getInstance();

        initViewModel();
        initProductAdapter();
        initSortSelectionViewModel();
        initTabListViewModel();
        initCategoryAdapter();

        getSelectedSortOption();
    }

    private void initTabListViewModel() {
        NavController navController = NavHostFragment.findNavController(requireParentFragment());
        NavBackStackEntry parentBackStackEntry = Objects.requireNonNull(navController.getCurrentBackStackEntry());
        productTabListViewModel = new ViewModelProvider(parentBackStackEntry).get(ProductTabListViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        observeViewModel();

        setupCategoryRecyclerView();
    }

    @Override
    public void onDestroyView() {
        binding.recyclerViewProduct.setAdapter(null);
        binding = null;
        super.onDestroyView();
    }

    /**
     * Get the saved sort option in the SharedPreference,other and set it to ViewModel.
     */
    private void getSelectedSortOption() {
        UserPreferenceManager userPreferenceManager = new UserPreferenceManager(requireContext());
        viewModel.setSortOption(userPreferenceManager.getSortOption());
    }

    /**
     * Initialize the SortSelectionViewModel to observe the change in the sort option select,
     * then triggering sorting the product list.
     */
    private void initSortSelectionViewModel() {
        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry navBackStackEntry = Objects.requireNonNull(navController.getCurrentBackStackEntry());
        ProductSortOptionRepository productSortOptionRepository = ProductSortOptionRepository.getInstance();
        ProductSortSelectionViewModelFactory sortSelectionViewModelFactory = new ProductSortSelectionViewModelFactory(productSortOptionRepository);
        sortSelectionViewModel = new ViewModelProvider(navBackStackEntry, sortSelectionViewModelFactory).get(ProductSortSelectionViewModel.class);
    }

    /**
     * Initialize the adapter for RecyclerView of the product list.
     */
    private void initProductAdapter() {
        adapter = new ProductListAdapter(productId -> {
            NavDirections action = ProductTabsFragmentDirections.actionToEdit(productId);
            NavHostFragment.findNavController(this).navigate(action);
        });
    }

    private void initViewModel() {
        NavController navController = NavHostFragment.findNavController(requireParentFragment());
        NavBackStackEntry parentBackStackEntry = Objects.requireNonNull(navController.getCurrentBackStackEntry());

        ProductRepository repository = ProductRepository.getInstance();
        ProductListViewModelFactory factory = new ProductListViewModelFactory(repository);
        viewModel = new ViewModelProvider(parentBackStackEntry, factory).get(ProductListViewModel.class);
    }

    /**
     * Set the adapter and optimization options for RecyclerView.
     */
    private void setupRecyclerView() {
        // Observe sort option, updating the state accordingly.
        sortSelectionViewModel.getSortOption().observe(getViewLifecycleOwner(), viewModel::setSortOption);

        // Change the layout based on the state.
        productTabListViewModel.getToggleLayout().observe(getViewLifecycleOwner(), this::setupRecyclerViewLayout);

        binding.recyclerViewProduct.setHasFixedSize(true);
        binding.recyclerViewProduct.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getProducts().observe(getViewLifecycleOwner(), products -> {
            adapter.submitList(null);
            adapter.submitList(products);
        });
    }

    private void setupRecyclerViewLayout(Boolean isGrid) {
        Context context = requireContext();

        // Remove the existing decorations.
        while (binding.recyclerViewProduct.getItemDecorationCount() > 0) {
            binding.recyclerViewProduct.removeItemDecorationAt(0);
        }

        if (isGrid) {
            GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 2);
            SpacingStrategy spacingStrategy = new GridSpacingStrategy(requireContext(), 8);
            binding.recyclerViewProduct.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
            binding.recyclerViewProduct.setLayoutManager(gridLayoutManager);
            adapter.setItemLayoutRes(R.layout.grid_item_product);
        } else {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            SpacingStrategy spacingStrategy = new LinearSpacingStrategy(requireContext(), 8);
            binding.recyclerViewProduct.addItemDecoration(new SpacingItemDecoration(spacingStrategy));
            binding.recyclerViewProduct.setLayoutManager(linearLayoutManager);
            adapter.setItemLayoutRes(R.layout.list_item_horizontal_product);
        }
    }

    private void initCategoryAdapter() {
        categoryAdapter = new CategoryTagSelectionAdapter(position -> viewModel.setCategory(
                position == RecyclerView.NO_POSITION ? null : categoryRepository.getCategoryByPosition(position)
        ));
    }

    private void setupCategoryRecyclerView() {
        EnumSet<LinearSpacingStrategy.Direction> directions = EnumSet.of(
                LinearSpacingStrategy.Direction.LEFT,
                LinearSpacingStrategy.Direction.RIGHT
        );
        SpacingStrategy spacingStrategy = new LinearSpacingStrategy(
                requireContext(), 8, directions
        );
        binding.rvCategory.addItemDecoration(new SpacingItemDecoration(spacingStrategy));

        binding.rvCategory.setAdapter(categoryAdapter);

        categoryRepository.getCategories().observe(
                getViewLifecycleOwner(), categoryAdapter::submitList
        );

        Category category = viewModel.getCategory().getValue();
        if (category != null) {
            int pos = categoryRepository.getPositionById(category.getId());
            categoryAdapter.setSelectedPosition(pos);
        }
    }

}