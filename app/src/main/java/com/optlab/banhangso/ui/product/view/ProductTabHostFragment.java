package com.optlab.banhangso.ui.product.view;

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
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;

import com.google.android.material.tabs.TabLayoutMediator;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductTabHostBinding;
import com.optlab.banhangso.ui.adapter.ProductViewPagerAdapter;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabHostSharedViewModel;
import com.optlab.banhangso.util.UserPreferenceManager;

import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

@AndroidEntryPoint
public class ProductTabHostFragment extends Fragment {
    @Inject protected UserPreferenceManager userPreferenceManager;

    private FragmentProductTabHostBinding binding;
    private ProductTabHostSharedViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViewModel();
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
                        if (position == 0) {
                            if (binding.toolBar.getMenu().size() == 0) {
                                binding.toolBar.inflateMenu(R.menu.menu_product_toolbar);
                                binding.toolBar.setOnMenuItemClickListener(
                                        item -> onProductMenuItemSelected(item));
                            }

                        } else {
                            // Clear the menu when switching to other tabs
                            binding.toolBar.getMenu().clear();
                        }
                    }
                });
    }

    private boolean onProductMenuItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_toggle_layout) {
            userPreferenceManager.saveLayoutMode(viewModel.toggleLayout());
        } else {
            NavHostFragment.findNavController(this).navigate(R.id.productSortSelectionFragment);
        }
        return true;
    }
}
