package com.optlab.banhangso.features.main.transaction.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.transaction.models.TransactionSummaryUiModel;
import com.optlab.banhangso.models.domain.TransactionSummary;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionSummaryUiModelMapper {

  @NonNull public static TransactionSummaryUiModel fromDomain(
      @NonNull TransactionSummary transactionSummary) {
    return new TransactionSummaryUiModel(
        transactionSummary.getId(),
        transactionSummary.getCustomerName(),
        transactionSummary.getStaffName(),
        transactionSummary.getPrice(),
        transactionSummary.getCreatedAt());
  }
}
