package com.optlab.banhangso.features.main.sale.models;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import java.util.List;
import java.util.Locale;
import lombok.Data;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@Data
public class ReceiptUiModel {

  private final String id;
  private final int totalItems;
  private final String cashierName;
  private final String customerName;
  private final String totalPrices;
  private final String note;
  private final String createdAt;
  private final List<Item> items;

  public String getId() {
    return this.id;
  }

  public int getTotalItems() {
    return this.totalItems;
  }

  public String getCashierName() {
    return this.cashierName;
  }

  public String getCustomerName() {
    return this.customerName;
  }

  public String getTotalPrices() {
    return this.totalPrices;
  }

  public String getNote() {
    return this.note;
  }

  public String getCreatedAt() {
    return this.createdAt;
  }

  public List<Item> getItems() {
    return this.items;
  }

  @Data
  public static class Item {

    private final String id;
    private final String name;
    private final double sellingPrice;
    private final double discountPrice;
    private final String displayUnitPrice;
    private final double finalPrices;
    private final String displayFinalPrices;
    private final int quantity;
    private final String displayQuantity;

    public Item(
        @NonNull String id,
        @NonNull String name,
        double sellingPrice,
        double discountPrice,
        int quantity) {
      this.id = id;
      this.name = name;
      this.quantity = quantity;
      this.discountPrice = discountPrice;
      this.sellingPrice = sellingPrice;
      this.displayQuantity = formatQuantity(quantity);
      this.displayUnitPrice = formatUnitPrice(sellingPrice, discountPrice);
      this.finalPrices = calculateFinalPrices(sellingPrice, discountPrice, quantity);
      this.displayFinalPrices = formatFinalPrices(sellingPrice, discountPrice, quantity);
    }

    @NonNull private static String formatUnitPrice(double sellingPrice, double discountPrice) {
      double price = discountPrice > 0.0 ? discountPrice : sellingPrice;
      return PriceFormatter.withSuffix(price);
    }

    private static double calculateFinalPrices(
        double sellingPrice, double discountPrice, int quantity) {
      double price = discountPrice > 0.0 ? discountPrice : sellingPrice;
      return quantity * price;
    }

    @NonNull private static String formatFinalPrices(
        double sellingPrice, double discountPrice, int quantity) {
      double total = calculateFinalPrices(sellingPrice, discountPrice, quantity);
      return PriceFormatter.withSuffix(total);
    }

    @NonNull private static String formatQuantity(int quantity) {
      return String.format(Locale.getDefault(), "x%d", quantity);
    }

    public String getId() {
      return this.id;
    }

    public String getName() {
      return this.name;
    }

    public double getSellingPrice() {
      return this.sellingPrice;
    }

    public double getDiscountPrice() {
      return this.discountPrice;
    }

    public String getDisplayUnitPrice() {
      return this.displayUnitPrice;
    }

    public double getFinalPrices() {
      return this.finalPrices;
    }

    public String getDisplayFinalPrices() {
      return this.displayFinalPrices;
    }

    public int getQuantity() {
      return this.quantity;
    }

    public String getDisplayQuantity() {
      return this.displayQuantity;
    }
  }
}
