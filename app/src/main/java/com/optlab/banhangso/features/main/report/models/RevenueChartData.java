package com.optlab.banhangso.features.main.report.models;

import androidx.annotation.NonNull;
import com.github.mikephil.charting.data.BarEntry;
import com.optlab.banhangso.models.application.PriceUnit;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class RevenueChartData extends LabeledChartData<BarEntry, String> {
  @NonNull private final PriceUnit unit;

  public RevenueChartData(
      @NonNull List<BarEntry> data, @NonNull List<String> labels, @NonNull PriceUnit unit) {
    super(data, labels);
    this.unit = unit;
  }
}
