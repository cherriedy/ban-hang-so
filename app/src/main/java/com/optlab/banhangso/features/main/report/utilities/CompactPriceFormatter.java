package com.optlab.banhangso.features.main.report.utilities;

import androidx.annotation.NonNull;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.optlab.banhangso.internal.Config;
import com.optlab.banhangso.models.application.PriceUnit;

public class CompactPriceFormatter extends ValueFormatter {
  private final PriceUnit priceUnit;

  public CompactPriceFormatter(PriceUnit priceUnit) {
    this.priceUnit = priceUnit;
  }

  @Override
  public String getBarLabel(@NonNull BarEntry barEntry) {
    return formatCompact(barEntry.getY());
  }

  @Override
  public String getFormattedValue(float value) {
    return formatCompact(value);
  }

  private String formatCompact(double value) {
    double displayValue = value / priceUnit.getValue();
    if (priceUnit == PriceUnit.NONE || value == 0.0) {
      return String.valueOf((int) value);
    }
    return String.format(Config.VIETNAM_LOCALE, "%.1f%s", displayValue, priceUnit.getSuffix());
  }
}
