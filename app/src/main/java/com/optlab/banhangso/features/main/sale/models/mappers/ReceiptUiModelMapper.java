package com.optlab.banhangso.features.main.sale.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.sale.models.ReceiptUiModel;
import com.optlab.banhangso.internal.Config;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import com.optlab.banhangso.models.domain.TransactionRecord;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReceiptUiModelMapper {

  @NonNull public static ReceiptUiModel fromTransaction(@NonNull TransactionRecord transactionRecord) {
    String displayTotalPrices = PriceFormatter.withSuffix(transactionRecord.getFinalPrices());

    String displayCreatedAt =
        new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault())
            .format(transactionRecord.getCreatedAt());

    List<ReceiptUiModel.Item> items =
        ReceiptItemUiModelMapper.fromTransactions(transactionRecord.getItems());

    return new ReceiptUiModel(
        transactionRecord.getId(),
        transactionRecord.getTotalItems(),
        transactionRecord.getStaff().getName(),
        transactionRecord.getCustomer().getName(),
        displayTotalPrices,
        transactionRecord.getNote(),
        displayCreatedAt,
        items);
  }
}
