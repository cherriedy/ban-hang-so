package com.optlab.banhangso.ui.brand.view;

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
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.databinding.FragmentSortSelectionBinding;
import com.optlab.banhangso.ui.adapter.SortSelectionAdapter;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSortSelectionViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class BrandSortSelectionFragment extends BottomSheetDialogFragment {
    private FragmentSortSelectionBinding binding;
    private BrandSortSelectionViewModel viewModel;
    private ProductTabHostSharedViewModel tabHostSharedViewModel;
    private SortSelectionAdapter<Brand.SortField> adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initAdapter();
    }

    private void initAdapter() {
        adapter =
                new SortSelectionAdapter<>(
                        sortOption -> {
                            // Set the selected sort option in the ViewModel
                            viewModel.setSortOptionIndex(sortOption);
                            // Set the selected sort option in the shared ViewModel
                            tabHostSharedViewModel.setBrandSortOption(sortOption);
                        });
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(BrandSortSelectionViewModel.class);

        NavBackStackEntry productTabsEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        tabHostSharedViewModel =
                new ViewModelProvider(productTabsEntry).get(ProductTabHostSharedViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSortSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModels();
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        binding.rvSortSelection.setHasFixedSize(true);
        binding.rvSortSelection.setAdapter(adapter);
    }

    private void observeViewModels() {
        viewModel.getSortOptions().observe(getViewLifecycleOwner(), adapter::submitList);

        viewModel
                .getSortOptionIndex()
                .observe(getViewLifecycleOwner(), adapter::setCheckedPosition);
    }
}
