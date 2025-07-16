package com.optlab.banhangso.features.main.sale.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.sale.models.ReceiptUiModel;
import com.optlab.banhangso.models.domain.TransactionRecord;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReceiptItemUiModelMapper {

  @NonNull public static ReceiptUiModel.Item fromTransaction(@NonNull TransactionRecord.Item item) {
    return new ReceiptUiModel.Item(
        item.getId(),
        item.getName(),
        item.getSellingPrice(),
        item.getDiscountPrice(),
        item.getQuantity());
  }

  @NonNull public static List<ReceiptUiModel.Item> fromTransactions(
      @NonNull List<TransactionRecord.Item> items) {
    return items.stream()
        .map(ReceiptItemUiModelMapper::fromTransaction)
        .collect(Collectors.toList());
  }
}
