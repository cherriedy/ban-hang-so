package com.optlab.banhangso.features.main.report.views;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.BottomSheetReportModuleFiltersBinding;
import com.optlab.banhangso.features.main.report.viewmodels.ReportModuleViewModel;
import com.optlab.banhangso.features.shared.views.filters.helpers.IntervalFilterRestorer;
import com.optlab.banhangso.features.shared.views.filters.interval.FilterIntervalView;
import com.optlab.banhangso.models.application.Interval;

public class ReportFilterBottomSheet extends BottomSheetDialogFragment {

  private BottomSheetReportModuleFiltersBinding binding;
  private ReportModuleViewModel viewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    NavController navController = NavHostFragment.findNavController(requireParentFragment());
    NavBackStackEntry stackEntry = navController.getBackStackEntry(R.id.reportModuleFragment);
    viewModel = new ViewModelProvider(stackEntry).get(ReportModuleViewModel.class);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
    binding =
        BottomSheetReportModuleFiltersBinding.inflate(
            requireActivity().getLayoutInflater(), null, false);

    // Restore the interval filter state from the ViewModel.
    IntervalFilterRestorer.restore(
        binding.ftInterval,
        viewModel.getInterval(),
        viewModel.getDateFrom(),
        viewModel.getDateTo());

    registerIntervalFilterChangedListener(); // Register listener for interval filter changes.
    binding.ibClose.setOnClickListener(v -> dialog.dismiss());
    binding.mbClear.setOnClickListener(v -> binding.ftInterval.onReset());
    dialog.setContentView(binding.getRoot());
    return dialog;
  }

  private void registerIntervalFilterChangedListener() {
    binding.ftInterval.addOnFilterChangedListener(
        new FilterIntervalView.OnFilterChangedListener() {
          @Override
          public void onIntervalFilterChanged(@Nullable Interval interval) {
            viewModel.setInterval(interval);
          }

          @Override
          public void onDateFromChanged(@NonNull String date) {
            binding.ftInterval.clearSelectedInterval();
            viewModel.setDateFrom(date);
          }

          @Override
          public void onDateToChanged(@NonNull String date) {
            binding.ftInterval.clearSelectedInterval();
            viewModel.setDateTo(date);
          }
        });
  }
}
