package com.optlab.banhangso.features.main.home.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.home.models.ReportSummaryUiModel;
import com.optlab.banhangso.internal.utilities.DateTimeUtils;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import com.optlab.banhangso.models.domain.ReportSummary;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ReportSummaryUiModelMapper {

  @NonNull public static ReportSummaryUiModel fromDomain(@NonNull ReportSummary domain) {
    String dateStr = DateTimeUtils.forDisplay(domain.getDate());
    String revenue = PriceFormatter.withSuffix(domain.getRevenue());
    return new ReportSummaryUiModel(
        revenue,
        String.valueOf(domain.getTransactions()),
        String.valueOf(domain.getCustomers()),
        dateStr);
  }
}
