package com.optlab.banhangso.models.remote;

import androidx.annotation.Nullable;
import java.util.Date;
import java.util.List;

public record SaleReportDto(
    @Nullable String currency,
    @Nullable String granularity,
    @Nullable Double revenue,
    @Nullable Double profit,
    @Nullable Double cost,
    @Nullable DateRange dateRange,
    @Nullable RevenueByDate revenueByDate,
    @Nullable TransactionsByDate transactionsByDate,
    @Nullable Summary summary) {

  public record DateRange(@Nullable Date startDate, @Nullable Date endDate) {}

  public record RevenueByDate(@Nullable String unit, @Nullable List<Data> data) {
    public record Data(@Nullable Date date, @Nullable Double value) {}
  }

  public record TransactionsByDate(@Nullable List<Data> data) {
    public record Data(@Nullable Date date, @Nullable Integer value) {}
  }

  public record Summary(
      @Nullable Double averageRevenue,
      @Nullable Double maxRevenue,
      @Nullable Integer totalTransactions,
      @Nullable String unit) {}
}
