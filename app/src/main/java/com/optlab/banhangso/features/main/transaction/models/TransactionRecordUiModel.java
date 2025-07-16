package com.optlab.banhangso.features.main.transaction.models;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import java.util.List;
import java.util.Locale;
import lombok.Data;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@Data
public class TransactionRecordUiModel {
  private final String id;
  private final int totalItems;
  private final Customer customer;
  private final Staff staff;
  private final String finalPrices;
  private final String totalSellingPrices;
  private final String totalDiscountPrices;
  private final String note;
  private final int paymentMethod;
  private final String createdAt;
  private final List<Item> items;

  public String getId() {
    return this.id;
  }

  public int getTotalItems() {
    return this.totalItems;
  }

  public Customer getCustomer() {
    return customer;
  }

  public Staff getStaff() {
    return staff;
  }

  public String getFinalPrices() {
    return this.finalPrices;
  }

  public String getNote() {
    return this.note;
  }

  @StringRes
  public int getPaymentMethod() {
    return paymentMethod;
  }

  public String getCreatedAt() {
    return this.createdAt;
  }

  public List<Item> getItems() {
    return this.items;
  }

  @Data
  public static class Customer {

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
  }

  @Data
  public static class Staff {

    private final String id;
    private final String name;
    private final String phone;
    private final String email;
    private final String role;
  }

  @Data
  public static class Item {

    private final String id;
    private final String name;
    private final String brandName;
    private final String categoryName;
    private final double sellingPrice;
    private final double discountPrice;
    private final String displayUnitPrice;
    private final double finalPrices;
    private final String displayFinalPrices;
    private final int quantity;
    private final String displayQuantity;
    private final String thumbnailUrl;

    public Item(
        @NonNull String id,
        @NonNull String name,
        @NonNull String brandName,
        @NonNull String categoryName,
        double sellingPrice,
        double discountPrice,
        int quantity,
        @NonNull String thumbnailUrl) {
      this.id = id;
      this.name = name;
      this.brandName = brandName;
      this.categoryName = categoryName;
      this.quantity = quantity;
      this.discountPrice = discountPrice;
      this.sellingPrice = sellingPrice;
      this.displayQuantity = formatQuantity(quantity);
      this.thumbnailUrl = thumbnailUrl;
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

    public String getThumbnailUrl() {
      return thumbnailUrl;
    }
  }
}
