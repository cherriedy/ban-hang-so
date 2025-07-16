package com.optlab.banhangso.features.main.sale.models;

import static com.optlab.banhangso.features.main.sale.Constants.MAX_QUANTITY;
import static com.optlab.banhangso.features.main.sale.Constants.MIN_QUANTITY;
import static com.optlab.banhangso.internal.utilities.PriceFormatter.withSuffix;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.optlab.banhangso.BR;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.models.application.Payment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed, LombokSetterMayBeUsed
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CartUiModel extends BaseObservable {

  private final LinkedHashMap<String, Item> items = new LinkedHashMap<>();

  private CustomerUiModel customer;

  private double totalDiscountPrices = 0.0;
  private String displayTotalDiscountPrices;

  private double totalPurchasePrices = 0.0;
  private String displayTotalPurchasePrices;

  private double totalSellingPrices = 0.0;
  private String displayTotalSellingPrice;

  private double finalPrices = 0.0;
  private String displayFinalPrices;

  private int totalItems = 0;

  private String note;

  private Payment.Method paymentMethod = Payment.Method.CASH;

  public CustomerUiModel getCustomer() {
    return customer;
  }

  public void setCustomer(@NonNull CustomerUiModel customer) {
    this.customer = customer;
  }

  @Bindable
  public double getTotalSellingPrices() {
    return totalSellingPrices;
  }

  public void setTotalSellingPrices(double totalSellingPrices) {
    this.totalSellingPrices = totalSellingPrices;
    this.displayTotalSellingPrice = withSuffix(totalSellingPrices);
    notifyPropertyChanged(BR.totalSellingPrices);
    notifyPropertyChanged(BR.displayTotalSellingPrice);
  }

  @Bindable
  public String getDisplayTotalSellingPrice() {
    return displayTotalSellingPrice;
  }

  @Bindable
  public double getTotalDiscountPrices() {
    return totalDiscountPrices;
  }

  @Bindable
  public String getDisplayTotalDiscountPrice() {
    return displayTotalDiscountPrices;
  }

  public void setTotalDiscountPrices(double totalDiscountPrices) {
    this.totalDiscountPrices = totalDiscountPrices;
    this.displayTotalDiscountPrices = withSuffix(totalDiscountPrices);
    notifyPropertyChanged(BR.totalDiscountPrices);
    notifyPropertyChanged(BR.displayTotalDiscountPrice);
  }

  @Bindable
  public double getTotalPurchasePrices() {
    return totalPurchasePrices;
  }

  @Bindable
  public String getDisplayTotalPurchasePrices() {
    return displayTotalPurchasePrices;
  }

  public void setTotalPurchasePrices(double totalPurchasePrices) {
    this.totalPurchasePrices = totalPurchasePrices;
    this.displayTotalPurchasePrices = withSuffix(totalPurchasePrices);
    notifyPropertyChanged(BR.totalPurchasePrices);
    notifyPropertyChanged(BR.displayTotalPurchasePrices);
  }

  @Bindable
  public double getFinalPrices() {
    return finalPrices;
  }

  public void setFinalPrices(double finalPrices) {
    this.finalPrices = finalPrices;
    this.displayFinalPrices = withSuffix(finalPrices);
    notifyPropertyChanged(BR.finalPrices);
    notifyPropertyChanged(BR.displayFinalPrices);
  }

  @Bindable
  public String getDisplayFinalPrices() {
    return displayFinalPrices;
  }

  @NonNull public Map<String, Item> getItems() {
    return items;
  }

  @Bindable
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
    notifyPropertyChanged(BR.note);
  }

  @Bindable
  public int getTotalItems() {
    return totalItems;
  }

  public void setTotalItems(int totalItems) {
    this.totalItems = totalItems;
    notifyPropertyChanged(BR.totalItems);
  }

  public Payment.Method getPaymentType() {
    return paymentMethod;
  }

  public void setPaymentMethod(@NonNull Payment.Method paymentMethod) {
    this.paymentMethod = paymentMethod;
  }

  public Payment.Method getPaymentMethod() {
    return paymentMethod;
  }

  public void refreshCart() {
    updateTotalPurchasePrices();
    updateTotalSellingPrices();
    updateTotalDiscountPrices();
    updateFinalPrices();
    updateTotalItems();
  }

  private void updateTotalItems() {
    setTotalItems(items.values().stream().mapToInt(Item::getQuantity).sum());
  }

  private void updateTotalPurchasePrices() {
    setTotalDiscountPrices(
        items.values().stream()
            .mapToDouble(
                item -> {
                  int quantity = item.getQuantity();
                  Double sellingPrice = item.getSellingPrice();
                  Double discountPrice = item.getDiscountPrice();
                  double selling = sellingPrice != null ? sellingPrice : 0.0;
                  double discount = discountPrice != null ? discountPrice : 0.0;
                  return discount > 0.0 ? quantity * selling - discount : 0.0;
                })
            .sum());
  }

  private void updateTotalSellingPrices() {
    setTotalSellingPrices(
        items.values().stream()
            .mapToDouble(
                item -> {
                  Double sellingPrice = item.getSellingPrice();
                  return item.getQuantity() * (sellingPrice != null ? sellingPrice : 0.0);
                })
            .sum());
  }

  private void updateTotalDiscountPrices() {
    totalDiscountPrices =
        items.values().stream()
            .mapToDouble(
                item -> {
                  Double discountPrice = item.getDiscountPrice();
                  return discountPrice != null ? discountPrice : 0.0;
                })
            .sum();
  }

  private void updateFinalPrices() {
    setFinalPrices(
        items.values().stream()
            .mapToDouble(
                item -> {
                  Double sellingPrice = item.getSellingPrice();
                  Double discountPrice = item.getDiscountPrice();
                  double selling = sellingPrice != null ? sellingPrice : 0.0;
                  double discount = discountPrice != null ? discountPrice : 0.0;
                  double pricePerUnit = discount > 0 ? discount : selling;
                  return item.getQuantity() * pricePerUnit;
                })
            .sum());
  }

  public List<Item> asList() {
    return new ArrayList<>(items.values());
  }

  @Data
  @NoArgsConstructor
  @EqualsAndHashCode(callSuper = true)
  public static class Item extends BaseObservable {

    private String id;
    private String name;
    private String thumbnailUrl;
    private boolean status;

    private Double purchasePrice;

    private Double discountPrice;
    private String displayDiscountPrice;

    private Double sellingPrice;
    private String displaySellingPrice;

    private int quantity;

    public Item(
        String id,
        String name,
        String thumbnailUrl,
        Double sellingPrice,
        Double discountPrice,
        Double purchasePrice,
        boolean status,
        int quantity) {
      this.id = id;
      this.name = name;
      this.thumbnailUrl = thumbnailUrl;
      this.sellingPrice = sellingPrice;
      this.discountPrice = discountPrice;
      this.purchasePrice = purchasePrice;
      this.status = status;
      this.quantity = quantity;
      updateDisplayPrices();
    }

    @NonNull public Item copy() {
      return new Item(
          this.id,
          this.name,
          this.thumbnailUrl,
          this.sellingPrice,
          this.discountPrice,
          this.purchasePrice,
          this.status,
          this.quantity);
    }

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getThumbnailUrl() {
      return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
      this.thumbnailUrl = thumbnailUrl;
    }

    public Double getSellingPrice() {
      return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
      this.sellingPrice = sellingPrice;
      displaySellingPrice = withSuffix(sellingPrice != null ? sellingPrice : 0.0);
      Timber.d("sellingPrice: %s", displaySellingPrice);
    }

    public Double getDiscountPrice() {
      return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
      this.discountPrice = discountPrice;
      displayDiscountPrice = withSuffix(discountPrice != null ? discountPrice : 0.0);
      Timber.d("discountPrice: %s", displayDiscountPrice);
    }

    public Double getPurchasePrice() {
      return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
      this.purchasePrice = purchasePrice;
    }

    public boolean getStatus() {
      return status;
    }

    public void setStatus(boolean status) {
      this.status = status;
    }

    @Bindable
    public int getQuantity() {
      return quantity;
    }

    public void setQuantity(int quantity) {
      if (quantity < MIN_QUANTITY) quantity = MIN_QUANTITY;
      if (quantity > MAX_QUANTITY) quantity = MAX_QUANTITY;
      this.quantity = quantity;
      notifyPropertyChanged(BR.quantity);
    }

    public void decQuantity() {
      setQuantity(quantity - 1);
    }

    public void incQuantity() {
      setQuantity(quantity + 1);
    }

    public String getDisplayDiscountPrice() {
      return displayDiscountPrice;
    }

    public String getDisplaySellingPrice() {
      return displaySellingPrice;
    }

    private void updateDisplayPrices() {
      this.displayDiscountPrice = withSuffix(getDiscountPrice() != null ? getDiscountPrice() : 0.0);
      this.displaySellingPrice = withSuffix(getSellingPrice() != null ? getSellingPrice() : 0.0);
    }
  }
}
