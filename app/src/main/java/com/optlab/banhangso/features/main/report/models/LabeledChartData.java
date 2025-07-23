package com.optlab.banhangso.features.main.report.models;

import androidx.annotation.NonNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Represents chart data with associated labels.
 *
 * @param <D> Type of the data entries in the chart.
 * @param <L> Type of the labels associated with the data entries.
 */
@Data
@AllArgsConstructor
public abstract class LabeledChartData<D, L> {
  @NonNull private final List<D> entries;
  @NonNull private final List<L> labels;
}
