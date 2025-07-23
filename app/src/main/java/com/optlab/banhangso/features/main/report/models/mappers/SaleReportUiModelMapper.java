package com.optlab.banhangso.features.main.report.models.mappers;

import static com.optlab.banhangso.internal.Config.DATE_FORMAT;
import static com.optlab.banhangso.internal.Config.VIETNAM_LOCALE;
import static com.optlab.banhangso.internal.utilities.PriceFormatter.withSuffix;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.report.models.SaleReportUiModel;
import com.optlab.banhangso.models.application.PriceUnit;
import com.optlab.banhangso.models.domain.SaleReport;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class SaleReportUiModelMapper {

  private static final SimpleDateFormat SIMPLE_DATE_FORMAT =
      new SimpleDateFormat(DATE_FORMAT, VIETNAM_LOCALE);

  @NotNull public static SaleReportUiModel fromDomain(@NotNull SaleReport domain) {
    String revenue = withSuffix(domain.getRevenue());
    String profit = withSuffix(domain.getProfit());
    String cost = withSuffix(domain.getCost());

    return new SaleReportUiModel(
        domain.getCurrency(),
        domain.getGranularity(),
        revenue,
        profit,
        cost,
        mapDateRange(domain.getDateRange()),
        mapRevenueByDate(domain.getRevenueByDate()),
        mapTransactionByDate(domain.getTransactionsByDate()),
        mapSummary(domain.getSummary()));
  }

  @NotNull private static SaleReportUiModel.DateRange mapDateRange(@NotNull SaleReport.DateRange dateRange) {
    String startDate = SIMPLE_DATE_FORMAT.format(dateRange.getStart());
    String endDate = SIMPLE_DATE_FORMAT.format(dateRange.getEnd());
    return new SaleReportUiModel.DateRange(startDate, endDate);
  }

  @NonNull private static SaleReportUiModel.RevenueByDate mapRevenueByDate(
      @NotNull SaleReport.RevenueByDate revenueByDate) {

    var data =
        revenueByDate.getData().stream()
            .map(SaleReportUiModelMapper::mapRevenueByDateData)
            .collect(Collectors.toList());

    PriceUnit unit = PriceUnit.fromString(revenueByDate.getUnit());
    return new SaleReportUiModel.RevenueByDate(data, unit);
  }

  @NonNull private static SaleReportUiModel.RevenueByDate.Data mapRevenueByDateData(
      @NonNull SaleReport.RevenueByDate.Data d) {
    String date = SIMPLE_DATE_FORMAT.format(d.getDate());
    double value = d.getValue();
    return new SaleReportUiModel.RevenueByDate.Data(date, value);
  }

  @NonNull private static SaleReportUiModel.TransactionByDate mapTransactionByDate(
      @NonNull SaleReport.TransactionsByDate transactionByDate) {
    var data =
        transactionByDate.getData().stream()
            .map(SaleReportUiModelMapper::getData)
            .collect(Collectors.toList());

    return new SaleReportUiModel.TransactionByDate(data);
  }

  @NonNull private static SaleReportUiModel.TransactionByDate.Data getData(
      @NonNull SaleReport.TransactionsByDate.Data d) {
    String date = SIMPLE_DATE_FORMAT.format(d.getDate());
    int value = d.getValue();
    return new SaleReportUiModel.TransactionByDate.Data(date, value);
  }

  @NonNull @Contract("_ -> new")
  private static SaleReportUiModel.Summary mapSummary(@NonNull SaleReport.Summary summary) {
    PriceUnit unit = PriceUnit.fromString(summary.getUnit());

    return new SaleReportUiModel.Summary(
        summary.getAverageRevenue(), summary.getMaxRevenue(), summary.getTotalTransactions(), unit);
  }
}
