package com.optlab.banhangso.features.main.report.utilities;

import static com.optlab.banhangso.internal.Config.DATE_FORMAT;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;

import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import timber.log.Timber;

/**
 * CompactDateFormatter formats date labels for a bar chart.
 *
 * <p>This formatter takes a list of date strings in the format "yyyy-MM-dd" and converts them to a
 * more compact format "MM-dd" for display on the chart.
 */
public class CompactDateFormatter extends ValueFormatter {
  private final List<String> labels;
  private final SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_FORMAT, VIETNAM_LOCALE);
  private final SimpleDateFormat outputFormat = new SimpleDateFormat("MM-dd", VIETNAM_LOCALE);

  public CompactDateFormatter(List<String> labels) {
    this.labels = labels;
  }

  @Override
  public String getFormattedValue(float value) {
    int index = (int) value; // Convert float to int for index.
    if (index < 0 || index >= labels.size()) {
      return "";
    }

    try {
      String dateStr = labels.get(index);
      // Parse the date string using the input format.
      Date inputDate = inputFormat.parse(dateStr);
      return outputFormat.format(Objects.requireNonNull(inputDate));
    } catch (ParseException e) {
      Timber.e(e, "Failed to parse date: %s", labels.get((int) value));
      return "";
    }
  }
}
