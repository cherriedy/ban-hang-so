package com.optlab.banhangso.ui.category.view;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.ui.adapter.CategorySelectionAdapter;
import com.optlab.banhangso.ui.category.viewmodel.CategorySelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditSharedViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import java.util.Collections;

import javax.inject.Inject;

/**
 * Displays a list of categories for selection.
 *
 * <p>Fragment that displays a selectable list of categories with search functionality. This
 * fragment allows users to select a category and passes the selection back to the previous fragment
 * through the Navigation component's SavedStateHandle.
 */
@AndroidEntryPoint
public class CategorySelectionFragment extends Fragment {
    @Inject protected CategoryRepository repository;
    private FragmentOptionSelectionBinding binding;
    private CategorySelectionViewModel viewModel;
    private CategorySelectionAdapter adapter;
    private ProductEditSharedViewModel productEditSharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();

        // Initialize the RecyclerView adapter with a listener for item selection.
        adapter = new CategorySelectionAdapter(this::onItemSelected);
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(CategorySelectionViewModel.class);

        // Get the back stack entry for the product edit graph and retrieve the shared ViewModel.
        NavBackStackEntry productEditBackEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_edit);
        productEditSharedViewModel =
                new ViewModelProvider(productEditBackEntry).get(ProductEditSharedViewModel.class);
    }

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
        setupCategoryListAdapter();
        observeViewModels();
        handleSearchView();
    }

    private void observeViewModels() {
        viewModel
                .getCategoriesSource()
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

    /**
     * Handles the selection of a category item. When an item is selected, it updates the
     * ProductEditSharedViewModel with the selected category's position.
     *
     * @param checkedPosition The position of the selected category in the list.
     */
    private void onItemSelected(int checkedPosition) {
        if (checkedPosition != RecyclerView.NO_POSITION) {
            productEditSharedViewModel.setSelectCategoryPosition(checkedPosition);
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
        // Set the initial checked position in the adapter based on the category ID passed.
        adapter.setCheckedPosition(repository.getPositionById(args.getCategoryId()));

        binding.listOption.setAdapter(adapter);
        binding.listOption.setHasFixedSize(true);
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
                        // Update the search query in the ViewModel
                        viewModel.setSearchQuery(newText);
                        return true;
                    }
                });
    }
}
