package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Comparator;
import java.util.Date;

@IgnoreExtraProperties
public class Product extends BaseObservable implements Cloneable {
  public enum SortField {
    NAME("Tên A -> Z", "Tên Z -> A"),
    SELLING_PRICE("Giá từ thấp tới cao", "Giá từ cao tới thấp");

    private final String ascendingName;
    private final String descendingName;

    SortField(String ascendingName, String descendingName) {
      this.ascendingName = ascendingName;
      this.descendingName = descendingName;
    }

    public String getDisplayName(boolean isAscending) {
      return isAscending ? ascendingName : descendingName;
    }
  }

  public static Comparator<Product> getComparator(SortField field, boolean isAscending) {
    Comparator<Product> comparator =
        switch (field) {
          case NAME -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
          case SELLING_PRICE -> Comparator.comparingDouble(Product::getSellingPrice);
        };
    return isAscending ? comparator : comparator.reversed();
  }

  @Exclude private String id;
  private String barcode;
  private Category category;
  private Brand brand;
  private String name;
  private double purchasePrice;
  private double sellingPrice;
  private String avatarUrl;
  private int stockQuantity;
  private String description;
  private ProductStatus status;
  private double discountPrice;
  private String note;

  @ServerTimestamp private Date createdAt;
  @ServerTimestamp private Date updatedAt;

  public Product() {}

  private Product(@NonNull Builder builder) {
    this.id = builder.id;
    this.barcode = builder.barcode;
    this.category = builder.category;
    this.brand = builder.brand;
    this.name = builder.name;
    this.purchasePrice = builder.purchasePrice;
    this.sellingPrice = builder.sellingPrice;
    this.avatarUrl = builder.avatarUrl;
    this.stockQuantity = builder.stockQuantity;
    this.description = builder.description;
    this.status = builder.status;
    this.discountPrice = builder.discountPrice;
    this.note = builder.note;
  }

  @NonNull
  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  @Exclude
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  @Bindable
  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
    notifyPropertyChanged(BR.category);
  }

  @Bindable
  public Brand getBrand() {
    return brand;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
    notifyPropertyChanged(BR.brand);
  }

  @Bindable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    notifyPropertyChanged(BR.name);
  }

  @Bindable
  public double getPurchasePrice() {
    return purchasePrice;
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
    notifyPropertyChanged(BR.purchasePrice);
  }

  @Bindable
  public double getSellingPrice() {
    return sellingPrice;
  }

  public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
    notifyPropertyChanged(BR.sellingPrice);
  }

  @Bindable
  public int getStockQuantity() {
    return stockQuantity;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
    notifyPropertyChanged(BR.stockQuantity);
  }

  @Bindable
  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
    notifyPropertyChanged(BR.avatarUrl);
  }

  @Bindable
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    notifyPropertyChanged(BR.description);
  }

  @Bindable
  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
    notifyPropertyChanged(BR.status);
  }

  @Bindable
  public double getDiscountPrice() {
    return discountPrice;
  }

  public void setDiscountPrice(double discountPrice) {
    this.discountPrice = discountPrice;
    notifyPropertyChanged(BR.discountPrice);
  }

  @Bindable
  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
    notifyPropertyChanged(BR.note);
  }

  public enum ProductStatus {
    IN_STOCK((byte) 1),
    OUT_STOCK((byte) 0);

    private final byte value;

    ProductStatus(byte value) {
      this.value = value;
    }

    public static ProductStatus fromValue(byte value) {
      for (ProductStatus status : ProductStatus.values()) {
        if (status.value == value) {
          return status;
        }
      }
      throw new IllegalArgumentException("Invalid status value");
    }

    public byte getValue() {
      return value;
    }
  }

  public static class Builder {
    private String id;
    private String barcode = "";
    private Category category;
    private Brand brand;
    private String name = "";
    private double purchasePrice;
    private double sellingPrice;
    private String avatarUrl = "";
    private int stockQuantity;
    private String description = "";
    private ProductStatus status = ProductStatus.OUT_STOCK;
    private double discountPrice;
    private String note = "";

    public Builder(String id) {
      this.id = id;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder category(Category category) {
      this.category = category;
      return this;
    }

    public Builder barcode(String barcode) {
      this.barcode = barcode;
      return this;
    }

    public Builder brand(Brand brand) {
      this.brand = brand;
      return this;
    }

    public Builder purchasePrice(double purchasePrice) {
      this.purchasePrice = purchasePrice;
      return this;
    }

    public Builder sellingPrice(double sellingPrice) {
      this.sellingPrice = sellingPrice;
      return this;
    }

    public Builder discountPrice(double discountPrice) {
      this.discountPrice = discountPrice;
      return this;
    }

    public Builder stockQuantity(int stockQuantity) {
      this.stockQuantity = stockQuantity;
      return this;
    }

    public Builder status(ProductStatus status) {
      this.status = status;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder note(String note) {
      this.note = note;
      return this;
    }

    public Builder avatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
      return this;
    }

    public Product build() {
      return new Product(this);
    }
  }
}
