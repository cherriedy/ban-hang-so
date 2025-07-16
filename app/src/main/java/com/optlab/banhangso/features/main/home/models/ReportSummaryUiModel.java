package com.optlab.banhangso.features.main.home.models;

import lombok.Data;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@Data
public class ReportSummaryUiModel {
  private final String revenue;
  private final String transactions;
  private final String customers;
  private final String date;

  public String getRevenue() {
    return revenue;
  }

  public String getTransactions() {
    return transactions;
  }

  public String getCustomers() {
    return customers;
  }

  public String getDate() {
    return date;
  }
}
