package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.TransactionSummary;
import com.optlab.banhangso.models.remote.TransactionSummaryFirebaseObject;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TransactionSummaryFirebaseObjectMapper {

  @NonNull public static TransactionSummary toDomain(@NonNull TransactionSummaryFirebaseObject obj) {
    String id = obj.getId() != null ? obj.getId() : "";
    String customerName = obj.getCustomerName() != null ? obj.getCustomerName() : "";
    String staffName = obj.getStaffName() != null ? obj.getStaffName() : "";
    double price = obj.getPrice() != null ? obj.getPrice() : 0.0;
    // Assign the default date to the epoch if createdAt is null.
    Date createdAt = obj.getCreatedAt() != null ? obj.getCreatedAt() : new Date(0);
    return new TransactionSummary(id, customerName, staffName, price, createdAt);
  }
}
