package com.optlab.banhangso.features.main.brand.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BrandUiModel extends BaseObservable implements Serializable {

  private String id;
  private String name;
  private int productCount;
  private Date createdAt;
  private Date updatedAt;

  @Bindable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    notifyPropertyChanged(BR.name);
  }

  public String getId() {
    return this.id;
  }

  public int getProductCount() {
    return this.productCount;
  }

  public Date getCreatedAt() {
    return this.createdAt;
  }

  public Date getUpdatedAt() {
    return this.updatedAt;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setProductCount(int productCount) {
    this.productCount = productCount;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String toString() {
    return "BrandUiModel(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", productCount="
        + this.getProductCount()
        + ", createdAt="
        + this.getCreatedAt()
        + ", updatedAt="
        + this.getUpdatedAt()
        + ")";
  }
}
