package com.optlab.banhangso.ui.brand.view;

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
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentBrandListBinding;
import com.optlab.banhangso.ui.adapter.BrandListAdapter;
import com.optlab.banhangso.ui.brand.viewmodel.BrandListViewModel;
import com.optlab.banhangso.ui.common.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.SpacingItemDecoration;
import com.optlab.banhangso.ui.product.view.ProductTabHostFragmentDirections;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.EnumSet;

@AndroidEntryPoint
public class BrandListFragment extends Fragment {
    private FragmentBrandListBinding binding;
    private BrandListViewModel viewModel;
    private BrandListAdapter adapter;
    private ProductTabHostSharedViewModel tabHostSharedViewModel;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navController = NavHostFragment.findNavController(this);
        initViewModels();
        initAdapters();
    }

    private void initAdapters() {
        adapter = new BrandListAdapter(id -> navigateToBrandEditFragment(id, false));
    }

    private void navigateToBrandEditFragment(String id, boolean isCreateMode) {
        navController.navigate(
                ProductTabHostFragmentDirections.actionToBrandEdit(id, isCreateMode));
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(BrandListViewModel.class);

        NavBackStackEntry productTabsEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        tabHostSharedViewModel =
                new ViewModelProvider(productTabsEntry).get(ProductTabHostSharedViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBrandListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModels();
    }

    private void observeViewModels() {
        viewModel.getBrands().observe(getViewLifecycleOwner(), adapter::submitList);

        tabHostSharedViewModel
                .getSearchQuery()
                .observe(getViewLifecycleOwner(), viewModel::setSearchQuery);
    }

    private void initRecyclerView() {
        binding.rvBrands.setHasFixedSize(true);
        binding.rvBrands.setAdapter(adapter);

        while (binding.rvBrands.getItemDecorationCount() > 0) {
            binding.rvBrands.removeItemDecorationAt(0);
        }
        binding.rvBrands.addItemDecoration(
                new SpacingItemDecoration(
                        new LinearSpacingStrategy(
                                requireContext(),
                                8,
                                EnumSet.of(
                                        LinearSpacingStrategy.Direction.LEFT,
                                        LinearSpacingStrategy.Direction.RIGHT))));
    }

    /**
     * @noinspection unused
     */
    public void onAddButtonClick(@NonNull View view) {
        navigateToBrandEditFragment("", true);
    }
}
