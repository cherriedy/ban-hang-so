package com.optlab.banhangso.features.main.transaction.views;

import static com.optlab.banhangso.internal.Config.DATE_FORMAT;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.BottomSheetTransactionFiltersBinding;
import com.optlab.banhangso.features.main.transaction.adapters.IntervalFilterListAdapter;
import com.optlab.banhangso.features.main.transaction.adapters.PaymentFilterListAdapter;
import com.optlab.banhangso.features.shared.views.DatePickerDialog;
import com.optlab.banhangso.internal.utilities.DateTimeUtils;
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import timber.log.Timber;

public class TransactionFiltersBottomSheet extends BottomSheetDialogFragment {

  public static final String GET_FILTERS_REQUEST = "GET_FILTERS_REQUEST";
  public static final String INTERVAL_FILTER_RESULT = "INTERVAL_FILTER_RESULT";
  public static final String DATE_FROM_REQUEST = "DATE_FROM_REQUEST";
  public static final String DATE_FROM_RESULT = "DATE_FROM_RESULT";
  public static final String DATE_TO_REQUEST = "DATE_TO_REQUEST";
  public static final String DATE_TO_RESULT = "DATE_TO_RESULT";
  public static final String PAYMENT_FILTER_RESULT = "PAYMENT_FILTER_RESULT";
  public static final String PRICE_RANGE_FROM = "PRICE_RANGE_FROM";
  public static final String PRICE_RANGE_TO = "PRICE_RANGE_TO";

  private final Map<String, Object> results = new HashMap<>();

  @Getter private final MutableLiveData<Double> priceFrom = new MutableLiveData<>();

  @Getter private final MutableLiveData<Double> priceTo = new MutableLiveData<>();
  private final MutableLiveData<Boolean> includeIntervalFlag = new MutableLiveData<>();

  private final IntervalFilterListAdapter intervalFiltersAdapter =
      new IntervalFilterListAdapter(
          interval -> {
            includeIntervalFlag.setValue(true); // Indicate that interval is included.
            results.put(INTERVAL_FILTER_RESULT, interval);
          });

  private final PaymentFilterListAdapter paymentFilterListAdapter =
      new PaymentFilterListAdapter(payment -> results.put(PAYMENT_FILTER_RESULT, payment));

  private final SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat(DATE_FORMAT, VIETNAM_LOCALE);

  private BottomSheetTransactionFiltersBinding binding;
  private DatePickerDialog datePickerFromDialog;
  private DatePickerDialog datePickerToDialog;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    datePickerFromDialog = DatePickerDialog.newInstance(DATE_FROM_REQUEST, DATE_FROM_RESULT);
    datePickerToDialog = DatePickerDialog.newInstance(DATE_TO_REQUEST, DATE_TO_RESULT);
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    BottomSheetDialog dialog = new BottomSheetDialog(requireContext());

    binding =
        BottomSheetTransactionFiltersBinding.inflate(
            LayoutInflater.from(requireContext()), null, false);

    binding.setLifecycleOwner(this); // Set lifecycle owner for data binding
    binding.setSheet(this); // Set the current instance as the sheet for data binding
    includeIntervalFlag.observe(
        this,
        flag -> {
          if (flag != null && !flag) {
            // Remove the selection from the interval filters when switching to date filters.
            intervalFiltersAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
          }
        });

    GridSpacingStrategy gridSpacingStrategy = new GridSpacingStrategy(requireContext(), 8);
    SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(gridSpacingStrategy);

    binding.rvIntervals.addItemDecoration(spacingItemDecoration);
    binding.rvIntervals.setAdapter(intervalFiltersAdapter);

    binding.rvPayments.addItemDecoration(spacingItemDecoration);
    binding.rvPayments.setAdapter(paymentFilterListAdapter);

    binding.ibClose.setOnClickListener(v -> dialog.dismiss());
    binding.ibDateDrop.setOnClickListener(v -> toggleDateSection());
    binding.ibPriceDrop.setOnClickListener(v -> togglePriceSection());
    binding.mbComplete.setOnClickListener(v -> returnResults());

    setupDateFilters();

    dialog.setContentView(binding.getRoot());

    return dialog;
  }

  @Override
  public void onDestroy() {
    datePickerToDialog = null;
    datePickerFromDialog = null;
    super.onDestroy();
  }

  private void returnResults() {
    Bundle args = new Bundle();

    Double priceToValue = priceTo.getValue();
    if (priceToValue != null && priceToValue > 0.0) {
      args.putDouble(PRICE_RANGE_TO, priceToValue);
    }

    Double priceFromValue = priceFrom.getValue();
    if (priceFromValue != null && priceFromValue > 0.0) {
      args.putDouble(PRICE_RANGE_FROM, priceFromValue);
    }

    if (Boolean.TRUE.equals(includeIntervalFlag.getValue())) {
      String intervalValue = (String) results.getOrDefault(INTERVAL_FILTER_RESULT, "");
      if (intervalValue != null && !intervalValue.isBlank()) {
        args.putString(INTERVAL_FILTER_RESULT, intervalValue);
      }
    } else {
      String dateFromValue = (String) results.getOrDefault(DATE_FROM_RESULT, "");
      if (dateFromValue != null && !dateFromValue.isBlank()) {
        args.putString(DATE_FROM_RESULT, dateFromValue);
      }

      String dateToValue = (String) results.getOrDefault(DATE_TO_RESULT, "");
      if (dateToValue != null && !dateToValue.isBlank()) {
        args.putString(DATE_TO_RESULT, dateToValue);
      }
    }

    String paymentMethodValue = (String) results.getOrDefault(PAYMENT_FILTER_RESULT, "");
    if (paymentMethodValue != null && !paymentMethodValue.isBlank()) {
      args.putString(PAYMENT_FILTER_RESULT, paymentMethodValue);
    }

    getParentFragmentManager().setFragmentResult(GET_FILTERS_REQUEST, args);
    this.dismiss(); // Dismiss the bottom sheet after returning results.
  }

  private void togglePriceSection() {
    boolean visibility = binding.llPriceContent.getVisibility() == View.VISIBLE;
    setAnimatedVisibility(binding.llPriceContent, visibility);
    binding.ibPriceDrop.setImageResource(
        !visibility ? R.drawable.ic_drop_up : R.drawable.ic_drop_down);
  }

  private void toggleDateSection() {
    boolean visibility = binding.llDateContent.getVisibility() == View.VISIBLE;
    setAnimatedVisibility(binding.llDateContent, visibility);
    binding.ibDateDrop.setImageResource(
        !visibility ? R.drawable.ic_drop_up : R.drawable.ic_drop_down);
  }

  private void setAnimatedVisibility(@NonNull View view, boolean visibility) {
    if (visibility) {
      view.animate()
          .alpha(0f)
          .setDuration(0)
          .withEndAction(() -> view.setVisibility(View.GONE))
          .start();
    } else {
      view.setAlpha(0f);
      view.setVisibility(View.VISIBLE);
      view.animate().alpha(1f).setDuration(200).start();
    }
  }

  private void setupDateFilters() {
    binding.tvDateFrom.setText(DateTimeUtils.getToday());
    binding.tvDateFrom.setOnClickListener(v -> displayDatePickerFrom());
    registerDateFromListener();

    binding.tvDateTo.setText(DateTimeUtils.getToday());
    binding.tvDateTo.setOnClickListener(v -> displayDatePickerTo());
    registerDateToListener();
  }

  private void registerDateToListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DATE_TO_REQUEST,
            this,
            (requestKey, result) -> {
              includeIntervalFlag.setValue(false); // Indicate that interval is not included.
              String dateToValue = result.getString((DATE_TO_RESULT));
              binding.tvDateTo.setText(dateToValue);

              results.put(DATE_TO_RESULT, dateToValue); // Store the dateTo result

              // Limit the date from to be before or equal to date to
              datePickerFromDialog =
                  DatePickerDialog.newInstance(
                      DATE_FROM_REQUEST, DATE_FROM_RESULT, Objects.requireNonNull(dateToValue));

              try {
                String dateFromValue = binding.tvDateFrom.getText().toString();
                Date dateFrom = simpleDateFormat.parse(dateFromValue);
                Date dateTo = simpleDateFormat.parse(dateToValue);

                // Check if the dateFrom is after dateTo, to adjust the dateFrom value.
                if (Objects.requireNonNull(dateFrom).after(dateTo)) {
                  Timber.d("dateFrom: %s is after dateTo: %s", dateFromValue, dateToValue);
                  binding.tvDateFrom.setText(dateToValue);
                } else {
                  Timber.d(
                      "dateFrom: %s is before or equal to dateTo: %s", dateFromValue, dateToValue);
                }
              } catch (ParseException | NullPointerException e) {
                Timber.e(e, "There was an error parsing the date: %s", e.getMessage());
              }
            });
  }

  private void registerDateFromListener() {
    getParentFragmentManager()
        .setFragmentResultListener(
            DATE_FROM_REQUEST,
            this,
            (requestKey, result) -> {
              includeIntervalFlag.setValue(false); // Indicate that interval is not included.
              String dateFromValue = result.getString((DATE_FROM_RESULT));
              binding.tvDateFrom.setText(dateFromValue);

              results.put(DATE_FROM_RESULT, dateFromValue); // Store the dateFrom result
            });
  }

  private void displayDatePickerTo() {
    if (!datePickerToDialog.isAdded()) {
      datePickerToDialog.show(
          getParentFragmentManager(), "datePickerToDialog_" + this.getClass().getSimpleName());
    }
  }

  private void displayDatePickerFrom() {
    if (!datePickerFromDialog.isAdded()) {
      datePickerFromDialog.show(
          getParentFragmentManager(), "datePickerFromDialog_" + this.getClass().getSimpleName());
    }
  }
}
