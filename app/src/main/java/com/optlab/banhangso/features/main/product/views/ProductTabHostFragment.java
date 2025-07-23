package com.optlab.banhangso.features.main.product.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import com.google.android.material.tabs.TabLayoutMediator;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentProductTabHostBinding;
import com.optlab.banhangso.features.main.product.adapters.ProductViewPagerAdapter;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import dagger.hilt.android.AndroidEntryPoint;
import javax.inject.Inject;

@AndroidEntryPoint
public class ProductTabHostFragment extends Fragment {

  private FragmentProductTabHostBinding binding;
  @Inject PreferencesRepository preferenceRepository;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentProductTabHostBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.mtb.setNavigationOnClickListener(
        v -> NavHostFragment.findNavController(this).navigateUp());
    initViewPager();
    setupTitle();
    setupTabLayout();
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

  private void setupTitle() {
    int[] tabTitles = {
      R.string.tab_title_product, R.string.tab_title_category, R.string.tab_title_brand
    };

    binding.viewPager.registerOnPageChangeCallback(
        new OnPageChangeCallback() {
          @Override
          public void onPageSelected(int position) {
            if (position >= 0 && position <= tabTitles.length) {
              binding.mtb.setTitle(getString(tabTitles[position]));
            }
          }
        });

    addVerticalDivider(); // Add the vertical divider to each item in TabLayout
  }

  private void addVerticalDivider() {
    LinearLayout linearLayout = (LinearLayout) binding.tabLayout.getChildAt(0);
    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    linearLayout.setDividerDrawable(
        ContextCompat.getDrawable(requireContext(), R.drawable.vertical_divider_tab_layout));
  }
}
