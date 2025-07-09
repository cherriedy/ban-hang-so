package com.optlab.banhangso.features.shared.views;

import static com.optlab.banhangso.internal.Config.DATE_FORMAT;

import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DatePickerDialog extends DialogFragment
    implements android.app.DatePickerDialog.OnDateSetListener {

  public static final String REQUEST = "request";
  public static final String RESULT = "date";

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    final Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int date = calendar.get(Calendar.DATE);

    android.app.DatePickerDialog dialog =
        new android.app.DatePickerDialog(requireContext(), this, year, month, date);

    // Set maximum date to today to prevent future date selection
    dialog.getDatePicker().setMaxDate(System.currentTimeMillis());

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

    Bundle data = new Bundle();
    data.putString(RESULT, dateFormat.format(selectedDate));
    getParentFragmentManager().setFragmentResult(REQUEST, data);
  }
}
