package com.optlab.banhangso.features.main.product.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.google.firebase.firestore.Exclude;
import com.optlab.banhangso.BR;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import java.util.Date;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

/**
 * @noinspection LombokSetterMayBeUsed, LombokGetterMayBeUsed
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUiModel extends BaseObservable {
  private String id;
  private String barcode;
  private Category category;
  private Brand brand;
  private String name;
  private double purchasePrice;
  private double sellingPrice;
  private String avatarUrl;
  private int stockQuantity;
  private String description;
  private boolean status;
  private double discountPrice;
  private String note;
  private Date createdAt;
  private Date updatedAt;

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof ProductUiModel that) {
      if (this == obj) return true;
      return id.equals(that.id)
          && barcode.equals(that.barcode)
          && category.equals(that.category)
          && brand.equals(that.brand)
          && name.equals(that.name)
          && purchasePrice == that.purchasePrice
          && sellingPrice == that.sellingPrice
          && avatarUrl.equals(that.avatarUrl)
          && stockQuantity == that.stockQuantity
          && description.equals(that.description)
          && status == that.status
          && discountPrice == that.discountPrice
          && note.equals(that.note);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        id,
        barcode,
        category,
        brand,
        name,
        purchasePrice,
        sellingPrice,
        avatarUrl,
        stockQuantity,
        description,
        status,
        discountPrice,
        note);
  }

  @NonNull @Override
  public String toString() {
    return "Product{"
        + "id='"
        + id
        + '\''
        + ", barcode='"
        + barcode
        + '\''
        + ", category="
        + category
        + ", brand="
        + brand
        + ", name='"
        + name
        + '\''
        + ", purchasePrice="
        + purchasePrice
        + ", sellingPrice="
        + sellingPrice
        + ", avatarUrl='"
        + avatarUrl
        + '\''
        + ", stockQuantity="
        + stockQuantity
        + ", description='"
        + description
        + '\''
        + ", status="
        + status
        + ", discountPrice="
        + discountPrice
        + ", note='"
        + note
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
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
  public boolean getStatus() {
    return status;
  }

  public void setStatus(boolean status) {
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

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public Date getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }
}
