package com.optlab.banhangso.features.main.report.models;

import androidx.annotation.NonNull;
import com.github.mikephil.charting.data.BarEntry;
import java.util.List;

public final class TransactionChartData extends LabeledChartData<BarEntry, String> {
  public TransactionChartData(@NonNull List<BarEntry> entries, @NonNull List<String> labels) {
    super(entries, labels);
  }
}
