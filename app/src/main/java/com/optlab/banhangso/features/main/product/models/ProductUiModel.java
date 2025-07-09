package com.optlab.banhangso.features.main.product.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.optlab.banhangso.BR;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;

import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @noinspection LombokSetterMayBeUsed, LombokGetterMayBeUsed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductUiModel extends BaseObservable {
  private String id;
  private String barcode;
  private CategoryUiModel category;
  private BrandUiModel brand;
  private String name;
  private double purchasePrice;
  private double sellingPrice;
  private String thumbnailUrl;
  private List<String> imageUrls;
  private int stockQuantity;
  private String description;
  private boolean status;
  private double discountPrice;
  private String note;
  private Date createdAt;
  private Date updatedAt;

  @Bindable
  public CategoryUiModel getCategory() {
    return category;
  }

  public void setCategory(CategoryUiModel category) {
    this.category = category;
    notifyPropertyChanged(BR.category);
  }

  @Bindable
  public BrandUiModel getBrand() {
    return brand;
  }

  public void setBrand(BrandUiModel brand) {
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
}
