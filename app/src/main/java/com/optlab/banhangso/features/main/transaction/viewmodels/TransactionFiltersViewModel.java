package com.optlab.banhangso.features.main.transaction.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.application.FilterParams;
import com.optlab.banhangso.models.application.Interval;
import com.optlab.banhangso.models.application.Payment;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class TransactionFiltersViewModel extends ViewModel {

  private final MutableLiveData<Payment> payment = new MutableLiveData<>();
  private final MutableLiveData<Interval> interval = new MutableLiveData<>();
  private final MutableLiveData<String> dateFrom = new MutableLiveData<>();
  private final MutableLiveData<String> dateTo = new MutableLiveData<>();
  private final MutableLiveData<Double> priceFrom = new MutableLiveData<>();
  private final MutableLiveData<Double> priceTo = new MutableLiveData<>();
  private final MediatorLiveData<FilterParams> filterParams = new MediatorLiveData<>();

  @Inject
  public TransactionFiltersViewModel() {
    filterParams.addSource(payment, __ -> buildParams());
    filterParams.addSource(interval, __ -> buildParams());
    filterParams.addSource(dateFrom, __ -> buildParams());
    filterParams.addSource(dateTo, __ -> buildParams());
    filterParams.addSource(priceFrom, __ -> buildParams());
    filterParams.addSource(priceTo, __ -> buildParams());
  }

  private void buildParams() {
    FilterParams.FilterParamsBuilder builder = FilterParams.builder();

    Payment selectedPayment = payment.getValue();
    if (selectedPayment != null) {
      String method = selectedPayment.getValue();
      Timber.d("Current payment method: %s", method);
      if (!method.isBlank()) {
        builder.payment(method); // Set the payment method.
      }
    }

    Interval selectedInterval = interval.getValue();
    if (selectedInterval != null) {
      String value = selectedInterval.getValue();
      builder.endDate(value); // Set the end date to the interval value.
      builder.startDate(value); // Set the start date to the interval value.
    }

    String fromDate = dateFrom.getValue();
    Timber.d("Current date from: %s", fromDate);
    if (fromDate != null && !fromDate.isBlank()) {
      builder.startDate(fromDate);
    }

    String toDate = dateTo.getValue();
    Timber.d("Current date to: %s", toDate);
    if (toDate != null && !toDate.isBlank()) {
      builder.endDate(toDate);
    }

    Double fromPrice = priceFrom.getValue();
    Timber.d("Current price from: %s", fromPrice);
    if (fromPrice != null && fromPrice > 0.0) {
      builder.priceFrom(fromPrice);
    }

    Double toPrice = priceTo.getValue();
    Timber.d("Current price to: %s", toPrice);
    if (toPrice != null && toPrice > 0.0) {
      builder.priceTo(toPrice);
    }

    filterParams.setValue(builder.build());
  }

  public LiveData<FilterParams> getFilterParams() {
    return filterParams;
  }

  public void setPayment(Payment payment) {
    this.payment.setValue(payment);
  }

  public LiveData<Payment> getPayment() {
    return payment;
  }

  public void setInterval(Interval interval) {
    this.interval.setValue(interval);
  }

  public LiveData<Interval> getInterval() {
    return interval;
  }

  public void setDateFrom(String date) {
    dateFrom.setValue(date);
  }

  public LiveData<String> getDateFrom() {
    return dateFrom;
  }

  public void setDateTo(String date) {
    dateTo.setValue(date);
  }

  public LiveData<String> getDateTo() {
    return dateTo;
  }

  public void setPriceFrom(Double price) {
    priceFrom.setValue(price);
  }

  public LiveData<Double> getPriceFrom() {
    return priceFrom;
  }

  public void setPriceTo(Double price) {
    priceTo.setValue(price);
  }

  public LiveData<Double> getPriceTo() {
    return priceTo;
  }
}
