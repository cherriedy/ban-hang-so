package com.optlab.banhangso.ui.category.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentCategoryListBinding;
import com.optlab.banhangso.ui.adapter.CategoryListAdapter;
import com.optlab.banhangso.ui.category.viewmodel.CategoryListViewModel;
import com.optlab.banhangso.ui.common.decoration.LinearSpacingStrategy;
import com.optlab.banhangso.ui.common.decoration.SpacingItemDecoration;
import com.optlab.banhangso.ui.listener.OnCategoryClickListener;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;

import java.util.EnumSet;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class CategoryListFragment extends Fragment {
    private FragmentCategoryListBinding binding;
    private CategoryListViewModel viewModel;
    private ProductTabHostSharedViewModel tabHostSharedViewModel;
    private CategoryListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModels();
        initAdapters();
    }

    private void initAdapters() {
        adapter =
                new CategoryListAdapter(
                        new OnCategoryClickListener() {
                            @Override
                            public void onClick(String id) {}
                        });
    }

    private void initViewModels() {
        viewModel = new ViewModelProvider(this).get(CategoryListViewModel.class);

        NavBackStackEntry productTabHostEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        tabHostSharedViewModel =
                new ViewModelProvider(productTabHostEntry).get(ProductTabHostSharedViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initRecyclerView();
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel
                .getCategories()
                .observe(
                        getViewLifecycleOwner(),
                        categories -> {
                            adapter.submitList(null);
                            adapter.submitList(categories);
                        });

        tabHostSharedViewModel
                .getCategorySortOption()
                .observe(getViewLifecycleOwner(), viewModel::setSortOption);

        tabHostSharedViewModel
                .getSearchQuery()
                .observe(getViewLifecycleOwner(), viewModel::setSearchQuery);
    }

    private void initRecyclerView() {
        binding.rvCategories.setAdapter(adapter);
        binding.rvCategories.setHasFixedSize(true);

        while (binding.rvCategories.getItemDecorationCount() > 0) {
            binding.rvCategories.removeItemDecorationAt(0);
        }

        binding.rvCategories.addItemDecoration(
                new SpacingItemDecoration(
                        new LinearSpacingStrategy(
                                requireContext(),
                                4,
                                EnumSet.of(
                                        LinearSpacingStrategy.Direction.TOP,
                                        LinearSpacingStrategy.Direction.BOTTOM))));

        binding.rvCategories.addItemDecoration(
                new SpacingItemDecoration(
                        new LinearSpacingStrategy(
                                requireContext(),
                                8,
                                EnumSet.of(
                                        LinearSpacingStrategy.Direction.LEFT,
                                        LinearSpacingStrategy.Direction.RIGHT))));
    }

    public void onAddButtonClick(@NonNull View view) {}
}
