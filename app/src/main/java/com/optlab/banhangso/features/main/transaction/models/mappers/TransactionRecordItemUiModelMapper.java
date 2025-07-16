package com.optlab.banhangso.features.main.transaction.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel;
import com.optlab.banhangso.models.domain.TransactionRecord;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionRecordItemUiModelMapper {

  @NonNull public static TransactionRecordUiModel.Item fromDomain(@NonNull TransactionRecord.Item item) {
    return new TransactionRecordUiModel.Item(
        item.getId(),
        item.getName(),
        item.getBrand().getName(),
        item.getCategory().getName(),
        item.getSellingPrice(),
        item.getDiscountPrice(),
        item.getQuantity(),
        item.getThumbnailUrl());
  }

  @NonNull public static List<TransactionRecordUiModel.Item> fromDomains(
      @NonNull List<TransactionRecord.Item> items) {
    return items.stream()
        .map(TransactionRecordItemUiModelMapper::fromDomain)
        .collect(Collectors.toList());
  }
}
