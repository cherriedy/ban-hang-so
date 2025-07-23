package com.optlab.banhangso.features.main.transaction.views;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.BottomSheetTransactionFiltersBinding;
import com.optlab.banhangso.features.main.transaction.viewmodels.TransactionFiltersViewModel;
import com.optlab.banhangso.features.shared.views.filters.helpers.IntervalFilterRestorer;
import com.optlab.banhangso.features.shared.views.filters.helpers.PaymentFilterRestorer;
import com.optlab.banhangso.features.shared.views.filters.interval.FilterIntervalView;
import com.optlab.banhangso.features.shared.views.filters.payment.FilterPaymentView;
import com.optlab.banhangso.models.application.Interval;
import com.optlab.banhangso.models.application.Payment;

public class TransactionFiltersBottomSheet extends BottomSheetDialogFragment {

  private BottomSheetTransactionFiltersBinding binding;
  private TransactionFiltersViewModel viewModel;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    NavController navController = NavHostFragment.findNavController(requireParentFragment());
    // Scope the view model to the back stack entry of the transaction list fragment.
    NavBackStackEntry stackEntry = navController.getBackStackEntry(R.id.transactionListFragment);
    viewModel = new ViewModelProvider(stackEntry).get(TransactionFiltersViewModel.class);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

    // Inflate the layout for this bottom sheet dialog.
    binding =
        BottomSheetTransactionFiltersBinding.inflate(
            LayoutInflater.from(requireContext()), null, false);

    restoreSelectedIntervalFilters(); // Restore previously selected interval filters.
    restoreSelectedPaymentFilters(); // Restore previously selected payment filters.

    // Set up the listeners for the filter views.
    registerPaymentFiltersChangedListener();
    registerIntervalFiltersChangedListener();

    binding.ibClose.setOnClickListener(v -> dialog.dismiss());
    binding.mbClear.setOnClickListener(v -> binding.ftInterval.onReset());
    dialog.setContentView(binding.getRoot());
    return dialog;
  }

  private void restoreSelectedPaymentFilters() {
    PaymentFilterRestorer.restore(
        binding.ftPayment,
        viewModel.getPayment(),
        viewModel.getPriceFrom(),
        viewModel.getPriceTo());
  }

  private void restoreSelectedIntervalFilters() {
    IntervalFilterRestorer.restore(
        binding.ftInterval,
        viewModel.getInterval(),
        viewModel.getDateFrom(),
        viewModel.getDateTo());
  }

  private void registerIntervalFiltersChangedListener() {
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

  private void registerPaymentFiltersChangedListener() {
    binding.ftPayment.addOnFilterSelectedListener(
        new FilterPaymentView.OnFilterSelectedListener() {
          @Override
          public void onPaymentMethodChanged(@Nullable Payment payment) {
            viewModel.setPayment(payment);
          }

          @Override
          public void onPriceFromChanged(@Nullable Double price) {
            binding.ftPayment.clearPaymentMethod();
            viewModel.setPriceFrom(price);
          }

          @Override
          public void onPriceToChanged(@Nullable Double price) {
            binding.ftPayment.clearPaymentMethod();
            viewModel.setPriceTo(price);
          }
        });
  }
}
