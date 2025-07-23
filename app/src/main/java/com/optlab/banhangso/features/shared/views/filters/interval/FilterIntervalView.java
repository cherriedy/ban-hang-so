package com.optlab.banhangso.features.shared.views.filters.interval;

import static com.optlab.banhangso.features.shared.utilities.ViewUtils.setAnimatedVisibility;
import static com.optlab.banhangso.internal.Config.DATE_FORMAT;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FilterIntervalBinding;
import com.optlab.banhangso.features.shared.views.DatePickerDialog;
import com.optlab.banhangso.features.shared.views.filters.FilterResetCallback;
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.models.application.Interval;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import timber.log.Timber;

public class FilterIntervalView extends LinearLayout implements FilterResetCallback {

  public static final String DATE_FROM_REQUEST_KEY = "DATE_FROM_REQUEST";
  public static final String DATE_FROM_RESULT = "DATE_FROM_RESULT";
  public static final String DATE_TO_REQUEST_KEY = "DATE_TO_REQUEST";
  public static final String DATE_TO_RESULT = "DATE_TO_RESULT";

  private static final String DEFAULT_DATE_FROM = "__-__-__";

  public interface OnFilterChangedListener {
    void onIntervalFilterChanged(@Nullable Interval interval);

    void onDateFromChanged(@NonNull String date);

    void onDateToChanged(@NonNull String date);
  }

  private final Context context;
  private final FilterIntervalBinding binding;
  private final SimpleDateFormat simpleDateFormat =
      new SimpleDateFormat(DATE_FORMAT, VIETNAM_LOCALE);

  private DatePickerDialog datePickerFromDialog;
  private DatePickerDialog datePickerToDialog;
  private OnFilterChangedListener listener;

  public FilterIntervalView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    binding = FilterIntervalBinding.inflate(LayoutInflater.from(context), this, true);
    datePickerFromDialog = DatePickerDialog.newInstance(DATE_FROM_REQUEST_KEY, DATE_FROM_RESULT);
    datePickerToDialog = DatePickerDialog.newInstance(DATE_TO_REQUEST_KEY, DATE_TO_RESULT);

    binding.ibDateDrop.setOnClickListener(v -> toggleIntervalSection());

    setupDateFilters(); // Init the display of date filters and listeners.
    setupIntervalFilterList(); // Init the display of interval filters and listeners.
  }

  @Override
  public void onReset() {
    binding.tvDateFrom.setText(DEFAULT_DATE_FROM);
    binding.tvDateTo.setText(DEFAULT_DATE_FROM);
    setSelectedInterval(null); // Unselect any selected interval
    if (listener != null) {
      listener.onIntervalFilterChanged(null);
      listener.onDateFromChanged("");
      listener.onDateToChanged("");
    }
  }

  public void addOnFilterChangedListener(@NonNull OnFilterChangedListener onFilterChangedListener) {
    listener = onFilterChangedListener;
  }

  public void setSelectedInterval(@Nullable Interval interval) {
    IntervalFilterListAdapter listAdapter =
        (IntervalFilterListAdapter) binding.rvIntervals.getAdapter();
    if (listAdapter != null) {
      if (interval != null) {
        // Set the selected position based on the interval.
        listAdapter.setSelectedPosition(interval);
      } else {
        // If the interval is null, set the selected position to
        // NO_POSITION to unselect any interval.
        listAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
      }
    }
  }

  public void setSelectedDateFrom(@Nullable String date) {
    if (date != null) {
      binding.tvDateFrom.setText(date); // Set the date from text view to the provided date.

      // Update the date picker dialog with the previously selected date.
      datePickerFromDialog =
          new DatePickerDialog.Builder()
              .requestKet(DATE_FROM_REQUEST_KEY)
              .resultKey(DATE_FROM_RESULT)
              .defaultDate(date)
              .build();
    }
  }

  public void setSelectedDateTo(@Nullable String date) {
    if (date != null) {
      binding.tvDateTo.setText(date); // Set the date to text view to the provided date.

      // Update the date picker dialog with the previously selected date.
      datePickerToDialog =
          new DatePickerDialog.Builder()
              .requestKet(DATE_TO_REQUEST_KEY)
              .resultKey(DATE_TO_RESULT)
              .defaultDate(date)
              .build();

      adjustDateFrom(date); // Adjust the date from if it is after the date to
    }
  }

  public void clearSelectedInterval() {
    // Clear the selected interval by setting it to null.
    setSelectedInterval(null);
    if (listener != null) {
      listener.onIntervalFilterChanged(null);
    }
  }

  private void setupDateFilters() {
    // binding.tvDateFrom.setText(DateTimeUtils.getToday());
    binding.tvDateFrom.setText(DEFAULT_DATE_FROM);
    binding.tvDateFrom.setOnClickListener(v -> displayDatePickerFrom());
    registerDateFromListener(); // Get and send the date from value.

    // binding.tvDateTo.setText(DateTimeUtils.getToday());
    binding.tvDateTo.setText(DEFAULT_DATE_FROM);
    binding.tvDateTo.setOnClickListener(v -> displayDatePickerTo());
    registerDateToListener(); // Get and send the date to value.
  }

  private void setupIntervalFilterList() {
    IntervalFilterListAdapter listAdapter =
        new IntervalFilterListAdapter(
            interval -> {
              if (listener != null) {
                listener.onIntervalFilterChanged(interval);
              }
            });
    binding.rvIntervals.setAdapter(listAdapter);

    setupIntervalItemSpacing();
  }

  private void toggleIntervalSection() {
    boolean visibility = binding.llDateContent.getVisibility() == View.VISIBLE;
    setAnimatedVisibility(binding.llDateContent, visibility);
    binding.ibDateDrop.setImageResource(
        !visibility ? R.drawable.ic_drop_up : R.drawable.ic_drop_down);
  }

  private FragmentActivity getFragmentActivity() {
    if (context instanceof FragmentActivity fragmentActivity) {
      return fragmentActivity;
    }
    throw new IllegalStateException(
        "FilterIntervalView must be used in a FragmentActivity context");
  }

  private void setupIntervalItemSpacing() {
    GridSpacingStrategy gridSpacingStrategy = new GridSpacingStrategy(context, 8);
    SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(gridSpacingStrategy);
    binding.rvIntervals.addItemDecoration(spacingItemDecoration);
  }

  private void displayDatePickerTo() {
    if (!datePickerToDialog.isAdded()) {
      datePickerToDialog.show(
          getFragmentActivity().getSupportFragmentManager(),
          "datePickerToDialog_" + this.getClass().getSimpleName());
    }
  }

  private void displayDatePickerFrom() {
    if (!datePickerFromDialog.isAdded()) {
      datePickerFromDialog.show(
          getFragmentActivity().getSupportFragmentManager(),
          "datePickerFromDialog_" + this.getClass().getSimpleName());
    }
  }

  private void registerDateToListener() {
    FragmentActivity fragmentActivity = getFragmentActivity();
    fragmentActivity
        .getSupportFragmentManager()
        .setFragmentResultListener(
            DATE_TO_REQUEST_KEY,
            fragmentActivity,
            (requestKey, result) -> {
              String dateToValue = result.getString((DATE_TO_RESULT));
              binding.tvDateTo.setText(dateToValue);

              // Limit the date from to be before or equal to date to
              datePickerFromDialog =
                  DatePickerDialog.newInstance(
                      DATE_FROM_REQUEST_KEY, DATE_FROM_RESULT, Objects.requireNonNull(dateToValue));

              adjustDateFrom(dateToValue); // Adjust the date from if it is after the date to

              if (listener != null) {
                listener.onDateToChanged(dateToValue);
              }
            });
  }

  /**
   * Adjusts the date from value if it is after the date to value.
   *
   * @param dateToValue the date to value to compare against.
   */
  private void adjustDateFrom(String dateToValue) {
    try {
      String dateFromValue = binding.tvDateFrom.getText().toString();
      if (dateFromValue.isBlank() || dateToValue.isBlank()) {
        Timber.w("Date from or date to value is blank, skipping adjustment.");
        return;
      }

      Date dateFrom = simpleDateFormat.parse(dateFromValue);
      Date dateTo = simpleDateFormat.parse(dateToValue);

      // Check if the dateFrom is after dateTo, to adjust the dateFrom
      if (Objects.requireNonNull(dateFrom).after(dateTo)) {
        Timber.d("dateFrom: %s is after dateTo: %s", dateFromValue, dateToValue);
        binding.tvDateFrom.setText(dateToValue);
      } else {
        Timber.d("dateFrom: %s is before or equal to dateTo: %s", dateFromValue, dateToValue);
      }
    } catch (ParseException | NullPointerException e) {
      Timber.e(e, "There was an error parsing the date: %s", e.getMessage());
    }
  }

  private void registerDateFromListener() {
    FragmentActivity fragmentActivity = getFragmentActivity();
    fragmentActivity
        .getSupportFragmentManager()
        .setFragmentResultListener(
            DATE_FROM_REQUEST_KEY,
            fragmentActivity,
            (requestKey, result) -> {
              String dateFromValue = result.getString(DATE_FROM_RESULT);
              binding.tvDateFrom.setText(dateFromValue);

              // Limit the date to to be after or equal to date from
              datePickerToDialog =
                  DatePickerDialog.newInstance(
                      DATE_TO_REQUEST_KEY, DATE_TO_RESULT, Objects.requireNonNull(dateFromValue));

              try {
                String dateToValue = binding.tvDateTo.getText().toString();
                Date dateFrom = simpleDateFormat.parse(dateFromValue);
                Date dateTo = simpleDateFormat.parse(dateToValue);

                // Check if the dateTo is before dateFrom, to adjust the dateTo
                if (Objects.requireNonNull(dateTo).before(dateFrom)) {
                  Timber.d("dateTo: %s is before dateFrom: %s", dateToValue, dateFromValue);
                  binding.tvDateTo.setText(dateFromValue);
                } else {
                  Timber.d(
                      "dateTo: %s is after or equal to dateFrom: %s", dateToValue, dateFromValue);
                }
              } catch (ParseException | NullPointerException e) {
                Timber.e(e, "There was an error parsing the date: %s", e.getMessage());
              }

              if (listener != null) {
                listener.onDateFromChanged(dateFromValue);
              }
            });
  }
}
