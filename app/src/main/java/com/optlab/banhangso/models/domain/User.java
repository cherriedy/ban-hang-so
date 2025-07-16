package com.optlab.banhangso.models.domain;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.optlab.banhangso.BR;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends BaseObservable {
  private String id;
  private String name;
  private String phone;
  private String email;
  private String imageUrl;
  private List<Store> stores;
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

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  public static class Store {
    private String id;
    private String role;
  }
}
