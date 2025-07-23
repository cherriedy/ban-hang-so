package com.optlab.banhangso.features.main.report.models;

import com.optlab.banhangso.models.application.PriceUnit;
import java.util.List;
import lombok.Data;

@Data
public class SaleReportUiModel {
  private final String currency;
  private final String granularity;
  private final String revenue;
  private final String profit;
  private final String cost;
  private final DateRange dateRange;
  private final RevenueByDate revenueByDate;
  private final TransactionByDate transactionByDate;
  private final Summary summary;

  @Data
  public static class DateRange {
    private final String startDate;
    private final String endDate;
  }

  @Data
  public static class RevenueByDate {
    private final List<Data> data;
    private final PriceUnit unit;

    @lombok.Data
    public static class Data {
      private final String date;
      private final double value;
    }
  }

  @Data
  public static class TransactionByDate {
    private final List<Data> data;

    @lombok.Data
    public static class Data {
      private final String date;
      private final int value;
    }
  }

  @Data
  public static class Summary {
    private final double averageRevenue;
    private final double maxRevenue;
    private final int totalTransactions;
    private final PriceUnit unit;
  }
}
