package com.optlab.banhangso.features.main.transaction.models;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.DateTimeUtils;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import java.util.Date;
import lombok.Data;

@Data
public class TransactionSummaryUiModel {

  @NonNull private final String id;
  @NonNull private final String customerName;
  @NonNull private final String staffName;
  @NonNull private final String price;
  @NonNull private final String createdAt;

  public TransactionSummaryUiModel(
      @NonNull String id,
      @NonNull String customerName,
      @NonNull String staffName,
      @NonNull Double price,
      @NonNull Date createdAt) {
    this.id = id;
    this.customerName = customerName;
    this.staffName = staffName;
    this.price = PriceFormatter.withSuffix(price);
    this.createdAt = DateTimeUtils.forDisplay(createdAt);
  }
}
