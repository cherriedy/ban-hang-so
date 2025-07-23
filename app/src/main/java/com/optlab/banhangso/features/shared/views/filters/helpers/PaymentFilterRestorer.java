package com.optlab.banhangso.features.shared.views.filters.helpers;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import com.optlab.banhangso.features.shared.views.filters.payment.FilterPaymentView;
import com.optlab.banhangso.models.application.Payment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PaymentFilterRestorer {
  public static void restore(
      @NonNull FilterPaymentView filterView,
      @NonNull LiveData<Payment> paymentLiveData,
      @NonNull LiveData<Double> priceFromLiveData,
      @NonNull LiveData<Double> priceToLiveData) {
    Payment selectedPayment = paymentLiveData.getValue();
    filterView.setSelectedPaymentMethod(selectedPayment);

    Double selectedPriceFrom = priceFromLiveData.getValue();
    filterView.setSelectedPriceFrom(selectedPriceFrom);

    Double selectedPriceTo = priceToLiveData.getValue();
    filterView.setSelectedPriceTo(selectedPriceTo);
  }
}
