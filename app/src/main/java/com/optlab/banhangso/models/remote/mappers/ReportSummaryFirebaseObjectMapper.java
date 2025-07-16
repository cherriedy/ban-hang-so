package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.ReportSummary;
import com.optlab.banhangso.models.remote.ReportSummaryFirebaseObject;
import java.util.Date;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportSummaryFirebaseObjectMapper {

  @NonNull public static ReportSummary toDomain(@NonNull ReportSummaryFirebaseObject obj) {
    double revenue = obj.getRevenue() != null ? obj.getRevenue() : 0.0;
    int transactions = obj.getTransactions() != null ? obj.getTransactions() : 0;
    int customers = obj.getCustomers() != null ? obj.getCustomers() : 0;
    Date date = obj.getDate() != null ? obj.getDate() : new Date(0);
    return new ReportSummary(revenue, transactions, customers, date);
  }
}
