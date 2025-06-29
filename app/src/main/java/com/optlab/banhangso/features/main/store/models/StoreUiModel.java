package com.optlab.banhangso.features.main.store.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import java.util.Date;
import java.util.Objects;

/**
 * @noinspection LombokSetterMayBeUsed, LombokGetterMayBeUsed
 */
public class StoreUiModel extends BaseObservable {
  private String id;
  private String name;
  private String description;
  private String imageUrl;
  private Date createdAt;
  private Date updatedAt;

  public StoreUiModel() {}

  public StoreUiModel(
      String id, String name, String description, String imageUrl, Date createdAt, Date updatedAt) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.imageUrl = imageUrl;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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
  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
    notifyPropertyChanged(BR.description);
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
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

  @NonNull @Override
  public String toString() {
    return "Store{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", imageUrl='"
        + imageUrl
        + '\''
        + ", createdAt="
        + createdAt
        + ", updatedAt="
        + updatedAt
        + '}';
  }

  @Override
  public boolean equals(@Nullable Object obj) {
    if (obj instanceof StoreUiModel that) {
      if (this == that) {
        return true;
      }
      return Objects.equals(this.id, that.id)
          && Objects.equals(this.name, that.name)
          && Objects.equals(this.description, that.description)
          && Objects.equals(this.imageUrl, that.imageUrl)
          && Objects.equals(this.createdAt, that.createdAt)
          && Objects.equals(this.updatedAt, that.updatedAt);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, description, imageUrl, createdAt, updatedAt);
  }
}
