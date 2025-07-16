package com.optlab.banhangso.features.main.transaction.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel.Customer;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel.Staff;
import com.optlab.banhangso.internal.Config;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import com.optlab.banhangso.models.application.Payment;
import com.optlab.banhangso.models.domain.TransactionRecord;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionRecordUiModelMapper {

  @NonNull public static TransactionRecordUiModel fromDomain(@NonNull TransactionRecord transactionRecord) {
    String displayFinalPrices = PriceFormatter.withSuffix(transactionRecord.getFinalPrices());
    String displayTotalSellingPrices =
        PriceFormatter.withSuffix(transactionRecord.getTotalSellingPrices());
    String displayTotalDiscountPrices =
        PriceFormatter.withSuffix(transactionRecord.getTotalDiscountPrices());

    String displayCreatedAt =
        new SimpleDateFormat(Config.DATE_FORMAT, Locale.getDefault())
            .format(transactionRecord.getCreatedAt());

    List<TransactionRecordUiModel.Item> items =
        TransactionRecordItemUiModelMapper.fromDomains(transactionRecord.getItems());

    int paymentMethod = Payment.getMethod(transactionRecord.getPaymentMethod()).getName();

    Customer customer = mapCustomer(transactionRecord);

    Staff staff = mapStaff(transactionRecord);

    return new TransactionRecordUiModel(
        transactionRecord.getId(),
        transactionRecord.getTotalItems(),
        customer,
        staff,
        displayFinalPrices,
        displayTotalSellingPrices,
        displayTotalDiscountPrices,
        transactionRecord.getNote(),
        paymentMethod,
        displayCreatedAt,
        items);
  }

  @NonNull private static Customer mapCustomer(@NonNull TransactionRecord transactionRecord) {
    return new Customer(
        transactionRecord.getCustomer().getId(),
        transactionRecord.getCustomer().getName(),
        transactionRecord.getCustomer().getPhone(),
        transactionRecord.getCustomer().getEmail());
  }

  @NonNull private static Staff mapStaff(@NonNull TransactionRecord transactionRecord) {
    return new Staff(
        transactionRecord.getStaff().getId(),
        transactionRecord.getStaff().getName(),
        transactionRecord.getStaff().getPhone(),
        transactionRecord.getStaff().getEmail(),
        transactionRecord.getStaff().getRole());
  }
}
