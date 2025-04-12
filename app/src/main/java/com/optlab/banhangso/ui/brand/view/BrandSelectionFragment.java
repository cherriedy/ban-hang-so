package com.optlab.banhangso.ui.brand.view;

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
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.databinding.FragmentOptionSelectionBinding;
import com.optlab.banhangso.ui.adapter.BrandSelectionAdapter;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditSharedViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import java.util.Collections;

import javax.inject.Inject;

/**
 * Fragment that displays a selectable list of brands with search functionality. This fragment
 * allows users to select a brand and passes the selection back to the previous fragment through the
 * Navigation component's SavedStateHandle.
 */
@AndroidEntryPoint
public class BrandSelectionFragment extends Fragment {
    @Inject protected BrandRepository repository;
    private FragmentOptionSelectionBinding binding;
    private BrandSelectionViewModel viewModel;
    private BrandSelectionAdapter adapter;
    private ProductEditSharedViewModel productEditSharedViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();

        // Initialize the RecyclerView adapter with a listener for item selection.
        adapter = new BrandSelectionAdapter(this::onItemSelected);
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(BrandSelectionViewModel.class);

        // Get the NavBackStackEntry for the ProductEditSharedViewModel from the navigation graph.
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
        setupBrandListAdapter();
        observeViewModels();
        handleSearchView();
    }

    private void observeViewModels() {
        // Observe the checked position in the ViewModel and update the adapter accordingly.
        viewModel
                .getBrands()
                .observe(
                        getViewLifecycleOwner(),
                        brands -> {
                            if (brands == null || brands.isEmpty()) {
                                adapter.submitList(Collections.emptyList());
                                Timber.e("Brand list is empty or null.");
                            } else {
                                adapter.submitList(brands);
                            }
                        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Handles the selection of a brand item. When an item is selected, it updates the
     * ProductEditSharedViewModel with the selected brand's position.
     *
     * @param checkedPosition The position of the selected item in the RecyclerView.
     */
    private void onItemSelected(int checkedPosition) {
        if (checkedPosition != RecyclerView.NO_POSITION) {
            productEditSharedViewModel.setSelectBrandPosition(checkedPosition);
        }
    }

    /**
     * Sets up the RecyclerView adapter for displaying brands. Retrieves the brand ID from
     * arguments, initializes the adapter with the correct selection, and observes the brand list
     * from the ViewModel.
     */
    private void setupBrandListAdapter() {
        // Get the arguments passed to this fragment, specifically the brand ID.
        BrandSelectionFragmentArgs args = BrandSelectionFragmentArgs.fromBundle(requireArguments());
        // Set the initial checked position in the adapter based on the brand ID passed.
        adapter.setCheckedPosition(repository.getPositionById(args.getBrandId()));

        binding.listOption.setHasFixedSize(true);
        binding.listOption.setAdapter(adapter);
    }

    /** Update the search query in the ViewModel when the text changes. */
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
