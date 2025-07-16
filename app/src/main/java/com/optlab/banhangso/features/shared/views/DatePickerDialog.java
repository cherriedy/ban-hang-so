package com.optlab.banhangso.features.shared.views;

import static com.optlab.banhangso.internal.Config.DATE_FORMAT;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import timber.log.Timber;

public class DatePickerDialog extends DialogFragment
    implements android.app.DatePickerDialog.OnDateSetListener {

  public static final String REQUEST = "DATE_PICKER_REQUEST";
  public static final String RESULT = "DATE_PICKER_RESULT";
  public static final String MAX_DATE = "DATE_PICKER_MAX_DATE";

  @NonNull public static DatePickerDialog newInstance(@NonNull String request, @NonNull String result) {
    Bundle args = new Bundle();
    args.putString(REQUEST, request);
    args.putString(RESULT, result);
    DatePickerDialog datePickerDialog = new DatePickerDialog();
    datePickerDialog.setArguments(args);
    return datePickerDialog;
  }

  @NonNull public static DatePickerDialog newInstance(
      @NonNull String request, @NonNull String result, @NonNull String maxDate) {
    Bundle args = new Bundle();
    args.putString(REQUEST, request);
    args.putString(RESULT, result);
    args.putString(MAX_DATE, maxDate);
    DatePickerDialog datePickerDialog = new DatePickerDialog();
    datePickerDialog.setArguments(args);
    return datePickerDialog;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int date = calendar.get(Calendar.DATE);

    android.app.DatePickerDialog dialog =
        new android.app.DatePickerDialog(requireContext(), this, year, month, date);

    // Get the maximum date from arguments if provided.
    String maxDateValue = getArguments() != null ? getArguments().getString(MAX_DATE) : null;
    if (maxDateValue != null && !maxDateValue.isBlank()) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, VIETNAM_LOCALE);
      try {
        // Parse the max date string.
        Date maxDate = simpleDateFormat.parse(maxDateValue);
        // Set the maximum date on the DatePicker.
        dialog.getDatePicker().setMaxDate(Objects.requireNonNull(maxDate).getTime());
      } catch (ParseException | NullPointerException e) {
        Timber.e(e, "There was an error parsing the max date: %s", e.getMessage());
      }
    } else {
      // Set maximum date to today to prevent future date selection
      dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    return dialog;
  }

  @Override
  public void onDateSet(DatePicker datePicker, int year, int month, int date) {
    // Create a Calendar instance to set the selected date
    Calendar calendar = Calendar.getInstance();
    calendar.set(year, month, date);

    // Get the selected date as a Date object
    Date selectedDate = calendar.getTime();

    // Format the date to a string using the specified format
    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());

    Bundle data = new Bundle(); // Return data to the parent fragment manager
    Bundle args = getArguments(); // Get the arguments passed to the dialog

    if (args != null) {
      String request = args.getString(REQUEST);
      String result = args.getString(RESULT);
      Timber.d("onDateSet: request=%s, result=%s", request, result);
      data.putString(result, dateFormat.format(selectedDate));

      // If a specific result key is provided, use it to store the formatted date.
      if (result != null && !result.isBlank()) {
        data.putString(result, dateFormat.format(selectedDate));
      }

      // If a request key is provided, use it to set the fragment result.
      if (request != null && !request.isBlank()) {
        getParentFragmentManager().setFragmentResult(request, data);
      }
    } else {
      data.putString(RESULT, dateFormat.format(selectedDate));
      getParentFragmentManager().setFragmentResult(REQUEST, data);
    }
  }
}
