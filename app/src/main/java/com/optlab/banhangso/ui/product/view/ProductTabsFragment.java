package com.optlab.banhangso.ui.product.view;

import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.optlab.banhangso.R;
import com.optlab.banhangso.ui.adapter.ProductTabsAdapter;
import com.optlab.banhangso.databinding.FragmentProductTabsBinding;
import com.optlab.banhangso.factory.ProductListViewModelFactory;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.util.UserPreferenceManager;
import com.optlab.banhangso.ui.product.viewmodel.ProductListViewModel;
import com.optlab.banhangso.ui.product.viewmodel.ProductTabListViewModel;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Objects;

public class ProductTabsFragment extends Fragment {

    private FragmentProductTabsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProductTabsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewPagerAdapter();
        initToolBar();
        setupTabLayout();
        setUpQueryHintText();

        binding.viewPager.setUserInputEnabled(false);

        NavController navController = NavHostFragment.findNavController(this);
        NavBackStackEntry backStackEntry = Objects.requireNonNull(navController.getCurrentBackStackEntry());

        ProductRepository productRepository = ProductRepository.getInstance();
        ProductListViewModelFactory productListViewModelFactory = new ProductListViewModelFactory(productRepository);
        ProductListViewModel productListViewModel = new ViewModelProvider(
                backStackEntry,
                productListViewModelFactory
        ).get(ProductListViewModel.class);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productListViewModel.setSearchQuery(newText);
                return true;
            }
        });
    }

    private void initViewPagerAdapter() {
        ProductTabsAdapter adapter = new ProductTabsAdapter(this);
        binding.viewPager.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }

    private void setupTabLayout() {
        int[] tabTitleRes = {
                R.string.tab_title_product,
                R.string.tab_title_category,
                R.string.tab_title_brand
        };

        new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
            if (position >= 0 && position <= tabTitleRes.length) {
                tab.setText(getString(tabTitleRes[position]));
            }
        }).attach();
    }

    private void setUpQueryHintText() {
        int[] hintQueryRes = {
                R.string.hint_query_search_keyword,
                R.string.hint_query_category,
                R.string.hint_query_brand
        };

        binding.viewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (position >= 0 && position <= hintQueryRes.length) {
                    binding.searchView.setQueryHint(getString(hintQueryRes[position]));
                }
            }
        });

        // Add the vertical divider to each item in TabLayout.
        addVerticalDivider();
    }

    private void addVerticalDivider() {
        LinearLayout linearLayout = (LinearLayout) binding.tabLayout.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerDrawable(
                ContextCompat.getDrawable(requireContext(), R.drawable.vertical_divider_tab_layout)
        );
    }

    private void initToolBar() {
        NavController navController = NavHostFragment.findNavController(this);
        ProductTabListViewModel productTabListViewModel = getTabListViewModel(navController);

        UserPreferenceManager userPreferenceManager = new UserPreferenceManager(requireContext());
        boolean layoutMode = userPreferenceManager.getLayoutMode();
        productTabListViewModel.setToggleLayout(layoutMode);

        binding.toolBar.inflateMenu(R.menu.menu_product_toolbar);
        binding.toolBar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_button_1) {
                navController.navigate(R.id.productSortSelectionFragment);
            } else {
                productTabListViewModel.toggleLayout();
                Boolean currentLayoutMode = productTabListViewModel.getToggleLayout().getValue();
                userPreferenceManager.saveLayoutMode(currentLayoutMode);
            }
            return true;
        });
    }

    private ProductTabListViewModel getTabListViewModel(NavController navController) {
        NavBackStackEntry navBackStackEntry = Objects.requireNonNull(navController.getCurrentBackStackEntry());
        return new ViewModelProvider(navBackStackEntry).get(ProductTabListViewModel.class);
    }
}