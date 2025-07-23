package com.optlab.banhangso.features.main.report.utilities;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.optlab.banhangso.internal.Config;

public class IntegerValueFormatter extends ValueFormatter {
  @Override
  public String getFormattedValue(float value) {
    return String.format(Config.VIETNAM_LOCALE, "%d", (int) value);
  }
}
