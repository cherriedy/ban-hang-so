package com.optlab.banhangso.features.main.product.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.optlab.banhangso.features.main.brand.view.BrandListFragment;
import com.optlab.banhangso.features.main.category.view.CategoryListFragment;
import com.optlab.banhangso.features.main.product.views.ProductListFragment;

public class ProductViewPagerAdapter extends FragmentStateAdapter {

  public ProductViewPagerAdapter(@NonNull Fragment fragment) {
    super(fragment);
  }

  @NonNull @Override
  public Fragment createFragment(int position) {
    return switch (position) {
      case 0 -> new ProductListFragment();
      case 1 -> new CategoryListFragment();
      case 2 -> new BrandListFragment();
      default -> throw new IllegalStateException("Unknown position: " + position);
    };
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public boolean containsItem(long itemId) {
    return itemId >= 0 && itemId <= 3;
  }

  @Override
  public int getItemCount() {
    return 3;
  }
}
