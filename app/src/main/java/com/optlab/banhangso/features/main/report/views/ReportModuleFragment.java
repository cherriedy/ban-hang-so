package com.optlab.banhangso.features.main.report.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.android.material.tabs.TabLayoutMediator;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentReportModuleBinding;
import com.optlab.banhangso.features.main.report.adapters.ReportViewPagerAdapter;

public class ReportModuleFragment extends Fragment {

  private FragmentReportModuleBinding binding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentReportModuleBinding.inflate(inflater, container, false);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    initViewPager();
    setupTabLayout();
  }

  private void setupTabLayout() {
    int[] tabTitles = {R.string.sale, R.string.customer};

    new TabLayoutMediator(
            binding.tlReport,
            binding.vpReport,
            (tab, position) -> {
              if (position >= 0 && position < tabTitles.length) {
                tab.setText(getString(tabTitles[position]));
              }
            })
        .attach();

    // Add the vertical divider between tabs in the TabLayout.
    LinearLayout linearLayout = (LinearLayout) binding.tlReport.getChildAt(0);
    linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
    linearLayout.setDividerDrawable(
        ContextCompat.getDrawable(requireContext(), R.drawable.vertical_divider_tab_layout));
  }

  private void initViewPager() {
    ReportViewPagerAdapter reportViewPagerAdapter = new ReportViewPagerAdapter(this);
    binding.vpReport.setAdapter(reportViewPagerAdapter);
    binding.vpReport.setUserInputEnabled(false); // Disable swipe to change pages
  }
}
