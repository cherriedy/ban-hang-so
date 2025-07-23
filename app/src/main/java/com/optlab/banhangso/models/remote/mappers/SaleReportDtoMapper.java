package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.SaleReport;
import com.optlab.banhangso.models.remote.SaleReportDto;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;

@UtilityClass
public class SaleReportDtoMapper {

  /**
   * Maps a SaleReportDto object to a SaleReport domain model. Handles nullability and assigns
   * default values for missing fields. Each nested record is mapped using its own helper method.
   *
   * @param dto the SaleReportDto to map
   * @return SaleReport domain model
   */
  @NonNull public static SaleReport toDomain(@NonNull SaleReportDto dto) {
    SaleReport.DateRange dateRange = mapDateRange(dto.dateRange());
    SaleReport.RevenueByDate revenueByDate = mapRevenueByDate(dto.revenueByDate());
    SaleReport.TransactionsByDate transByDate = mapTransactionsByDate(dto.transactionsByDate());
    SaleReport.Summary summary = mapSummary(dto.summary());
    return new SaleReport(
        dto.currency() != null ? dto.currency() : "",
        dto.granularity() != null ? dto.granularity() : "",
        dateRange,
        dto.revenue() != null ? dto.revenue() : 0.0,
        dto.cost() != null ? dto.cost() : 0.0,
        dto.profit() != null ? dto.profit() : 0.0,
        revenueByDate,
        transByDate,
        summary);
  }

  /**
   * Maps SaleReportDto.DateRange to SaleReport.DateRange. Assigns default Date(0) if start or end
   * is null.
   */
  @NonNull @Contract("null -> new")
  private static SaleReport.DateRange mapDateRange(SaleReportDto.DateRange dateRangeDto) {
    if (dateRangeDto == null) {
      return new SaleReport.DateRange(new Date(0), new Date(0));
    }
    Date start = dateRangeDto.startDate() != null ? dateRangeDto.startDate() : new Date(0);
    Date end = dateRangeDto.endDate() != null ? dateRangeDto.endDate() : new Date(0);
    return new SaleReport.DateRange(start, end);
  }

  /**
   * Maps SaleReportDto.RevenueByDate to SaleReport.RevenueByDate. Maps each data entry and assigns
   * default values for nulls.
   */
  @NonNull @Contract("null -> new")
  private static SaleReport.RevenueByDate mapRevenueByDate(
      SaleReportDto.RevenueByDate revenueByDateDto) {
    if (revenueByDateDto == null) {
      return new SaleReport.RevenueByDate("", Collections.emptyList());
    }

    // If revenueByDateDto.data() is null, we return an empty list.
    // Otherwise, we map each SaleReportDto.RevenueByDate.Data to SaleReport.RevenueByDate.Data.
    List<SaleReport.RevenueByDate.Data> data =
        revenueByDateDto.data() != null
            ? revenueByDateDto.data().stream()
                .map(SaleReportDtoMapper::mapRevenueByDateData)
                .collect(Collectors.toList())
            : Collections.emptyList();

    return new SaleReport.RevenueByDate(
        revenueByDateDto.unit() != null ? revenueByDateDto.unit() : "", data);
  }

  /**
   * Maps SaleReportDto.RevenueByDate.Data to SaleReport.RevenueByDate.Data. Assigns default Date(0)
   * and 0.0 for nulls.
   */
  @NonNull @Contract("_ -> new")
  private static SaleReport.RevenueByDate.Data mapRevenueByDateData(
      @NonNull SaleReportDto.RevenueByDate.Data d) {
    Date date = d.date() != null ? d.date() : new Date(0);
    double value = d.value() != null ? d.value() : 0.0;
    return new SaleReport.RevenueByDate.Data(date, value);
  }

  /**
   * Maps SaleReportDto.TransactionByDate to SaleReport.TransactionsByDate. Maps each data entry and
   * assigns default values for nulls.
   */
  @NonNull @Contract("null -> new")
  private static SaleReport.TransactionsByDate mapTransactionsByDate(
      SaleReportDto.TransactionsByDate transactionsByDateDto) {
    if (transactionsByDateDto == null) {
      return new SaleReport.TransactionsByDate(Collections.emptyList());
    }

    List<SaleReport.TransactionsByDate.Data> data =
        transactionsByDateDto.data() != null
            ? transactionsByDateDto.data().stream()
                .map(SaleReportDtoMapper::getData)
                .collect(Collectors.toList())
            : Collections.emptyList();

    return new SaleReport.TransactionsByDate(data);
  }

  /**
   * Maps SaleReportDto.TransactionsByDate.Data to SaleReport.TransactionsByDate.Data. Assigns
   * default Date(0) and 0 for nulls.
   *
   * @param d the SaleReportDto.TransactionsByDate.Data to map
   * @return SaleReport.TransactionsByDate.Data with default values for nulls
   */
  @NonNull @Contract("_ -> new")
  private static SaleReport.TransactionsByDate.Data getData(
      @NonNull SaleReportDto.TransactionsByDate.Data d) {
    Date date = d.date() != null ? d.date() : new Date(0);
    int value = d.value() != null ? d.value() : 0;
    return new SaleReport.TransactionsByDate.Data(date, value);
  }

  /** Maps SaleReportDto.Summary to SaleReport.Summary. Assigns default values for nulls. */
  @NonNull @Contract("_ -> new")
  private static SaleReport.Summary mapSummary(SaleReportDto.Summary summaryDto) {
    if (summaryDto == null) {
      return new SaleReport.Summary(0.0, 0.0, 0, "");
    }
    return new SaleReport.Summary(
        summaryDto.averageRevenue() != null ? summaryDto.averageRevenue() : 0.0,
        summaryDto.maxRevenue() != null ? summaryDto.maxRevenue() : 0.0,
        summaryDto.totalTransactions() != null ? summaryDto.totalTransactions() : 0,
        summaryDto.unit() != null ? summaryDto.unit() : "");
  }
}
