package com.optlab.banhangso.features.shared.views.filters.helpers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.optlab.banhangso.features.shared.views.filters.interval.FilterIntervalView;
import com.optlab.banhangso.models.application.Interval;
import lombok.experimental.UtilityClass;

@UtilityClass
public class IntervalFilterRestorer {
  public static void restore(
      @NonNull FilterIntervalView filterView,
      @NonNull LiveData<Interval> intervalLiveData,
      @NonNull LiveData<String> dateFromLiveData,
      @NonNull LiveData<String> dateToLiveData) {
    Interval selectedInterval = intervalLiveData.getValue();
    filterView.setSelectedInterval(selectedInterval);

    String selectedDateFrom = dateFromLiveData.getValue();
    filterView.setSelectedDateFrom(selectedDateFrom);

    String selectedDateTo = dateToLiveData.getValue();
    filterView.setSelectedDateTo(selectedDateTo);
  }
}
