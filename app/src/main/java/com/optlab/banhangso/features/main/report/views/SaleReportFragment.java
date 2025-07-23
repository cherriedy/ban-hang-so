package com.optlab.banhangso.features.main.report.views;

import static com.optlab.banhangso.features.main.report.Constants.AXIS_LABEL_TEXT_SIZE;
import static com.optlab.banhangso.features.main.report.Constants.BAR_WIDTH;
import static com.optlab.banhangso.features.main.report.Constants.VALUE_TEXT_SIZE;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSaleReportBinding;
import com.optlab.banhangso.features.main.report.models.RevenueChartData;
import com.optlab.banhangso.features.main.report.models.TransactionChartData;
import com.optlab.banhangso.features.main.report.utilities.CompactDateFormatter;
import com.optlab.banhangso.features.main.report.utilities.CompactPriceFormatter;
import com.optlab.banhangso.features.main.report.utilities.IntegerValueFormatter;
import com.optlab.banhangso.features.main.report.viewmodels.ReportModuleViewModel;
import com.optlab.banhangso.features.main.report.viewmodels.SaleReportViewModel;
import com.optlab.banhangso.features.shared.views.LoadingDialog;
import com.optlab.banhangso.models.application.Interval;
import com.optlab.banhangso.models.application.PriceUnit;
import dagger.hilt.android.AndroidEntryPoint;
import java.util.List;
import timber.log.Timber;

@AndroidEntryPoint
public class SaleReportFragment extends Fragment {

  private final LoadingDialog loadingDialog = new LoadingDialog();
  private final IntervalRadioGroupListener radioGroupListener = new IntervalRadioGroupListener();

  private FragmentSaleReportBinding binding;
  private SaleReportViewModel viewModel;
  private Context context;
  private NavController navController;
  private ReportModuleViewModel moduleViewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    context = requireContext();
    navController = NavHostFragment.findNavController(this);
    viewModel = new ViewModelProvider(this).get(SaleReportViewModel.class);

    // Scope the view model to the back stack entry of the report module fragment.
    NavBackStackEntry stackEntry = navController.getBackStackEntry(R.id.reportModuleFragment);
    moduleViewModel = new ViewModelProvider(stackEntry).get(ReportModuleViewModel.class);
  }

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    binding = FragmentSaleReportBinding.inflate(inflater, container, false);
    binding.setLifecycleOwner(getViewLifecycleOwner());
    binding.setModuleViewModel(moduleViewModel);
    binding.setViewModel(viewModel);
    binding.setFragment(this);
    return binding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    binding.ibFilter.setOnClickListener(v -> showFilters());
    binding.rgMethods.setOnCheckedChangeListener(radioGroupListener);
    setupReportChart();
    observeViewModel();
  }

  private void showFilters() {
    NavDirections action = SaleReportFragmentDirections.actionToFilters();
    navController.navigate(action);
  }

  private void observeViewModel() {
    viewModel.isLoading().observe(getViewLifecycleOwner(), this::handleLoadingState);
    viewModel.getPriceChartData().observe(getViewLifecycleOwner(), this::setRevenueBarChart);
    moduleViewModel.getFilters().observe(getViewLifecycleOwner(), viewModel::setFilterParams);
    moduleViewModel.getInterval().observe(getViewLifecycleOwner(), this::updateIntervalSelection);
  }

  private void handleLoadingState(@NonNull Boolean isLoading) {
    if (isLoading && !loadingDialog.isAdded()) {
      loadingDialog.show(
          getChildFragmentManager(), "loading_dialog" + this.getClass().getSimpleName());
    } else if (loadingDialog.isAdded()) {
      loadingDialog.dismiss();
    }
  }

  /**
   * @noinspection unused
   */
  public void onChartTypeSelected(@NonNull RadioGroup group, int checkedId) {
    if (checkedId == R.id.mrb_revenue) {
      RevenueChartData revenueChartData = viewModel.getPriceChartData().getValue();
      if (revenueChartData != null) {
        setRevenueBarChart(revenueChartData);
      } else {
        binding.lcReport.setNoDataText(getString(R.string.no_data_available));
        binding.lcReport.invalidate(); // Refresh the chart to show no data message
      }
    } else if (checkedId == R.id.mrb_transactions) {
      TransactionChartData transactionsChartData = viewModel.getTransactionsChartData().getValue();
      if (transactionsChartData != null) {
        setTransactionsBarChart(transactionsChartData);
      } else {
        binding.lcReport.setNoDataText(getString(R.string.no_data_available));
        binding.lcReport.invalidate(); // Refresh the chart to show no data message
      }
    }
  }

  private void updateIntervalSelection(@Nullable Interval interval) {
    if (interval == null) {
      binding.rgMethods.clearCheck();
      return;
    }

    Interval.Date date = Interval.getDate(interval);
    Timber.d("Selected interval: %s", date);
    // Remove the listener to prevent recursive calls.
    binding.rgMethods.setOnCheckedChangeListener(null);

    binding.rgMethods.check(
        switch (date) {
          case TODAY -> R.id.mrb_today;
          case YESTERDAY -> R.id.mrb_yesterday;
          case THIS_MONTH -> R.id.mrb_this_month;
          default -> {
            binding.rgMethods.clearCheck();
            yield View.NO_ID;
          }
        });

    // Re-attach the listener after updating the selection.
    binding.rgMethods.setOnCheckedChangeListener(radioGroupListener);
  }

  private void setupReportChart() {
    binding.lcReport.getDescription().setEnabled(false); // Disable description text
    binding.lcReport.getXAxis().setGranularity(1f); // Ensure each bar is spaced correctly
    binding.lcReport.getXAxis().setLabelRotationAngle(45f); // Rotates labels 45 degrees
    binding.lcReport.getAxisLeft().setAxisMinimum(0f); // Prevent negative y-axis
    binding.lcReport.getAxisRight().setAxisMinimum(0f); // Prevent negative y-axis
    binding.lcReport.getXAxis().setTextSize(5f); // Set x-axis label text size
    binding.lcReport.getAxisRight().setEnabled(false); // Hide right Y axis
    binding.lcReport.setFitBars(true); // Expand bars to fit the chart width
    binding.lcReport.getAxisLeft().setTextSize(AXIS_LABEL_TEXT_SIZE);
  }

  private void setRevenueBarChart(@NonNull RevenueChartData revenueChartData) {
    List<BarEntry> entries = revenueChartData.getEntries();
    List<String> labels = revenueChartData.getLabels();
    PriceUnit priceUnit = revenueChartData.getUnit();

    String title =
        String.format(
            "%s (%s)", getString(R.string.revenue), getString(priceUnit.getNameStringRes()));
    BarDataSet barDataSet = new BarDataSet(entries, title);

    barDataSet.setColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark));
    barDataSet.setValueTextColor(ContextCompat.getColor(context, android.R.color.black));
    barDataSet.setValueTextSize(VALUE_TEXT_SIZE); // Set text size for values
    barDataSet.setDrawValues(true); // Show values on top of bars
    // Use CompactPriceFormatter to format the values before displaying them.
    barDataSet.setValueFormatter(new CompactPriceFormatter(priceUnit));

    BarData barData = new BarData(barDataSet);
    barData.setBarWidth(BAR_WIDTH);

    binding.lcReport.setData(barData); // Set the data for the chart
    binding.lcReport.getXAxis().setLabelCount(labels.size(), true);
    binding.lcReport.getXAxis().setValueFormatter(new CompactDateFormatter(labels));
    binding.lcReport.invalidate(); // Refresh the chart to display the new data.
  }

  private void setTransactionsBarChart(@NonNull TransactionChartData transactionsChartData) {
    List<BarEntry> entries = transactionsChartData.getEntries();
    List<String> labels = transactionsChartData.getLabels();

    BarDataSet barDataSet = new BarDataSet(entries, getString(R.string.transactions));
    barDataSet.setColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark));
    barDataSet.setValueTextColor(ContextCompat.getColor(context, android.R.color.black));
    barDataSet.setValueTextSize(VALUE_TEXT_SIZE); // Set text size for values
    barDataSet.setDrawValues(true); // Show values on top of bars
    barDataSet.setValueFormatter(new IntegerValueFormatter());

    BarData barData = new BarData(barDataSet);
    barData.setBarWidth(BAR_WIDTH);
    binding.lcReport.setData(barData); // Set the data for the chart

    // Disable the description text.
    binding.lcReport.getDescription().setEnabled(false);

    binding.lcReport.getXAxis().setLabelCount(labels.size(), true);

    binding.lcReport.getAxisLeft().setGranularity(1f); // Y axis steps by 1
    binding.lcReport.getAxisRight().setGranularity(1f); // Y axis steps by 1

    // Use CompactDateFormatter to format the x-axis labels.
    binding.lcReport.getXAxis().setValueFormatter(new CompactDateFormatter(labels));

    binding.lcReport.invalidate(); // Refresh the chart to display the new data.
  }

  private final class IntervalRadioGroupListener implements RadioGroup.OnCheckedChangeListener {
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
      if (checkedId == R.id.mrb_today) {
        moduleViewModel.setInterval(Interval.getInterval(Interval.Date.TODAY));
      } else if (checkedId == R.id.mrb_yesterday) {
        moduleViewModel.setInterval(Interval.getInterval(Interval.Date.YESTERDAY));
      } else if (checkedId == R.id.mrb_this_month) {
        moduleViewModel.setInterval(Interval.getInterval(Interval.Date.THIS_MONTH));
      }
    }
  }
}
