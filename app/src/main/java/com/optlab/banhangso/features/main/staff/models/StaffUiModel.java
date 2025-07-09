package com.optlab.banhangso.features.main.staff.models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import com.optlab.banhangso.BR;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StaffUiModel extends BaseObservable {

  private String id;
  private String storeId;
  private String email;
  private String name;
  private String phone;
  private String imageUrl;
  private String role;
  private String status;
  private Boolean active;
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
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
    notifyPropertyChanged(BR.email);
  }

  @Bindable
  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
    notifyPropertyChanged(BR.phone);
  }

  @Bindable
  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
    notifyPropertyChanged(BR.imageUrl);
  }
}
