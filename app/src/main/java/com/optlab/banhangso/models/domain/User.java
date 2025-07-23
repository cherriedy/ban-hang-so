package com.optlab.banhangso.models.domain;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.optlab.banhangso.BR;

import org.jetbrains.annotations.Contract;

import java.util.Date;
import java.util.List;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class User extends BaseObservable {
  private String id;
  private String name;
  private String phone;
  private String email;
  private String imageUrl;
  private List<Store> stores;
  private Date createdAt;
  private Date updatedAt;

  public User(
      String id,
      String name,
      String phone,
      String email,
      String imageUrl,
      List<Store> stores,
      Date createdAt,
      Date updatedAt) {
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.email = email;
    this.imageUrl = imageUrl;
    this.stores = stores;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public User() {}

  @Bindable
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
    notifyPropertyChanged(BR.name);
  }

  @Bindable
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    notifyPropertyChanged(BR.imageUrl);
  }

  @NonNull @Contract(" -> new")
  public static User empty() {
    return new User("", "", "", "", "", null, null, null);
  }

  public boolean isEmpty() {
    return this.id.isBlank()
        && this.name.isBlank()
        && this.phone.isBlank()
        && this.email.isBlank()
        && this.imageUrl.isBlank()
        && this.stores == null
        && this.createdAt == null
        && this.updatedAt == null;
  }

  public String getId() {
    return this.id;
  }

  public String getPhone() {
    return this.phone;
  }

  public String getEmail() {
    return this.email;
  }

  public List<Store> getStores() {
    return this.stores;
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

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setStores(List<Store> stores) {
    this.stores = stores;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public String toString() {
    return "User(id="
        + this.getId()
        + ", name="
        + this.getName()
        + ", phone="
        + this.getPhone()
        + ", email="
        + this.getEmail()
        + ", imageUrl="
        + this.getImageUrl()
        + ", stores="
        + this.getStores()
        + ", createdAt="
        + this.getCreatedAt()
        + ", updatedAt="
        + this.getUpdatedAt()
        + ")";
  }


  @EqualsAndHashCode
  public static class Store {
    private String id;
    private String role;

    public Store(String id, String role) {
      this.id = id;
      this.role = role;
    }

    public Store() {}

    public String getId() {
      return this.id;
    }

    public String getRole() {
      return this.role;
    }

    public void setId(String id) {
      this.id = id;
    }

    public void setRole(String role) {
      this.role = role;
    }

    public String toString() {
      return "User.Store(id=" + this.getId() + ", role=" + this.getRole() + ")";
    }
  }
}
