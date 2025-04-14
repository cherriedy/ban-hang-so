package com.optlab.banhangso.ui.product.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductSortSelectionBinding;
import com.optlab.banhangso.ui.adapter.ProductSortSelectionAdapter;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;

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
    private FragmentProductSortSelectionBinding binding;
    private ProductSortSelectionViewModel viewModel;
    private ProductSortSelectionAdapter adapter;
    private ProductTabHostSharedViewModel tabHostSharedViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initAdapter();
    }

    private void initAdapter() {
        // Initialize the adapter with a listener to handle sort option selection events.
        adapter =
                new ProductSortSelectionAdapter(
                        sortOption -> {
                            // Set the selected sort option in the ViewModel to update the UI.
                            viewModel.setSelectedSortOptionPosition(sortOption);

                            // Set the selected sort option in the shared ViewModel to communicate
                            // with the parent fragment.
                            tabHostSharedViewModel.setSelectedSortOption(sortOption);
                        });

        adapter.submitList(viewModel.getSortOptions()); // Set the initial list of sort options
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(ProductSortSelectionViewModel.class);

        NavBackStackEntry productTabsEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        tabHostSharedViewModel =
                new ViewModelProvider(productTabsEntry).get(ProductTabHostSharedViewModel.class);
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
        setupRecyclerView();
        observeViewModel();
    }

    private void setupRecyclerView() {
        binding.rvSortSelection.setHasFixedSize(true);
        binding.rvSortSelection.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel
                .getSelectedSortOptionPosition()
                .observe(getViewLifecycleOwner(), adapter::setCheckedPosition);
    }
}
