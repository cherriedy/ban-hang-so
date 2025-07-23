package com.optlab.banhangso.features.main.report.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.application.FilterParams;
import com.optlab.banhangso.models.application.Interval;

public class ReportModuleViewModel extends ViewModel {
  private final MutableLiveData<Interval> interval = new MutableLiveData<>();
  private final MutableLiveData<String> dateFrom = new MutableLiveData<>();
  private final MutableLiveData<String> dateTo = new MutableLiveData<>();
  private final MediatorLiveData<FilterParams> filters = new MediatorLiveData<>();

  public ReportModuleViewModel() {
    Interval thisMonth = Interval.getInterval(Interval.Date.THIS_MONTH);
    interval.setValue(thisMonth); // Default to "This Month" interval.

    filters.addSource(interval, __ -> buildFilters());
    filters.addSource(dateFrom, __ -> buildFilters());
    filters.addSource(dateTo, __ -> buildFilters());
  }

  private void buildFilters() {
    Interval currentInterval = interval.getValue();
    String fromDate = dateFrom.getValue();
    String toDate = dateTo.getValue();

    FilterParams filterParams = new FilterParams();
    if (currentInterval != null) {
      filterParams.setStartDate(currentInterval.getValue());
      filterParams.setEndDate(currentInterval.getValue());
    }
    if (fromDate != null && !fromDate.isEmpty()) {
      filterParams.setStartDate(fromDate);
    }
    if (toDate != null && !toDate.isEmpty()) {
      filterParams.setEndDate(toDate);
    }

    filters.setValue(filterParams);
  }

  @NonNull public LiveData<Interval> getInterval() {
    return interval;
  }

  public void setInterval(Interval interval) {
    this.interval.setValue(interval);
  }

  @NonNull public LiveData<String> getDateFrom() {
    return dateFrom;
  }

  public void setDateFrom(String dateFrom) {
    this.dateFrom.setValue(dateFrom);
  }

  @NonNull public LiveData<String> getDateTo() {
    return dateTo;
  }

  public void setDateTo(String dateTo) {
    this.dateTo.setValue(dateTo);
  }

  public void setFilters(FilterParams filterParams) {
    filters.setValue(filterParams);
  }

  @NonNull public LiveData<FilterParams> getFilters() {
    return filters;
  }
}
