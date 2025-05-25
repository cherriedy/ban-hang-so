package com.optlab.banhangso.ui.main.product.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayoutMediator;
import com.optlab.banhangso.R;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.databinding.FragmentProductTabHostBinding;
import com.optlab.banhangso.ui.base.adapter.ProductViewPagerAdapter;
import com.optlab.banhangso.ui.main.product.viewmodel.ProductTabHostSharedViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ProductTabHostFragment extends Fragment {
    @Inject
    protected PreferenceRepository preferenceRepository;

    private FragmentProductTabHostBinding binding;
    private ProductTabHostSharedViewModel viewModel;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
        navController = NavHostFragment.findNavController(this);
    }

    private void initViewModel() {
        NavBackStackEntry productTabsEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_product_tabs);
        viewModel =
                new ViewModelProvider(productTabsEntry).get(ProductTabHostSharedViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductTabHostBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPager();
        setupTabBehavior();
        setupToolBar();
        setupTabLayout();
        observeQueryText();
    }

    private void setupTabBehavior() {
        setupQueryHintText(); // Change the hint text based on the selected tab
        resetSearchView(); // Reset the search view when switching tabs
    }

    /** Resets the search view when switching tabs. This is done by registering a page change */
    private void resetSearchView() {
        binding.viewPager.registerOnPageChangeCallback(
                new OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        binding.searchView.setQuery("", true);
                    }
                });
    }

    private void observeQueryText() {
        binding.searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        viewModel.setSearchQuery(newText);
                        return true;
                    }
                });
    }

    private void initViewPager() {
        ProductViewPagerAdapter adapter = new ProductViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false); // Disable swipe to change tabs
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void setupTabLayout() {
        int[] tabTitleRes = {
            R.string.tab_title_product, R.string.tab_title_category, R.string.tab_title_brand
        };

        new TabLayoutMediator(
                        binding.tabLayout,
                        binding.viewPager,
                        (tab, position) -> {
                            if (position >= 0 && position <= tabTitleRes.length) {
                                tab.setText(getString(tabTitleRes[position]));
                            }
                        })
                .attach();
    }

    private void setupQueryHintText() {
        int[] hintQueryRes = {
            R.string.hint_query_search_keyword,
            R.string.hint_query_category,
            R.string.hint_query_brand
        };

        binding.viewPager.registerOnPageChangeCallback(
                new OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        if (position >= 0 && position <= hintQueryRes.length) {
                            binding.searchView.setQueryHint(getString(hintQueryRes[position]));
                        }
                    }
                });

        addVerticalDivider(); // Add the vertical divider to each item in TabLayout
    }

    private void addVerticalDivider() {
        LinearLayout linearLayout = (LinearLayout) binding.tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(
                ContextCompat.getDrawable(
                        requireContext(), R.drawable.vertical_divider_tab_layout));
    }

    private void setupToolBar() {
        binding.viewPager.registerOnPageChangeCallback(
                new OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        handleOnPageSelected(position);
                    }
                });
    }

    private void handleOnPageSelected(int position) {
        binding.toolBar.getMenu().clear();

        switch (position) {
            case 0 -> {
                binding.toolBar.inflateMenu(R.menu.menu_product_toolbar);
                binding.toolBar.setOnMenuItemClickListener(this::onProductMenuItemSelected);
            }
            case 1 -> {
                binding.toolBar.inflateMenu(R.menu.menu_category_toolbar);
                binding.toolBar.setOnMenuItemClickListener(this::onCategoryMenuItemSelected);
            }
            case 2 -> {
                binding.toolBar.inflateMenu(R.menu.menu_brand_toolbar);
                binding.toolBar.setOnMenuItemClickListener(this::onBrandMenuItemSelected);
            }
        }
    }

    private boolean onCategoryMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select_sort) {
            navController.navigate(R.id.categorySortSelectionFragment);
        }
        return true;
    }

    private boolean onBrandMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_select_sort) {
            navController.navigate(R.id.brandSortSelectionFragment);
        }
        return true;
    }

    private boolean onProductMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_layout) {
            //            userPreferenceRepository.setLayoutMode(viewModel.toggleProductLayout());
        } else {
            navController.navigate(R.id.productSortSelectionFragment);
        }
        return true;
    }
}
