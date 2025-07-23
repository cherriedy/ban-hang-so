package com.optlab.banhangso.features.main.report.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.optlab.banhangso.features.main.report.views.CustomerReportFragment;
import com.optlab.banhangso.features.main.report.views.SaleReportFragment;

public class ReportViewPagerAdapter extends FragmentStateAdapter {

  public ReportViewPagerAdapter(@NonNull Fragment fragment) {
    super(fragment);
  }

  @NonNull @Override
  public Fragment createFragment(int position) {
    return switch (position) {
      case 0 -> new SaleReportFragment();
      case 1 -> new CustomerReportFragment();
      default -> throw new IllegalStateException("Unexpected value: " + position);
    };
  }

  @Override
  public int getItemCount() {
    return 2;
  }
}
