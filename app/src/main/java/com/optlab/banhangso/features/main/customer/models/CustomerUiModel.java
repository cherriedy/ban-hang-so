package com.optlab.banhangso.features.main.customer.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;
import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Contract;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomerUiModel extends BaseObservable implements Serializable, Parcelable {

  private String id;
  private String name;
  private String email;
  private String phone;
  private String address;
  private String imageUrl;
  private String imageUri;
  private String dob;
  private Date createdAt;
  private Date updatedAt;

  protected CustomerUiModel(@NonNull Parcel in) {
    id = in.readString();
    name = in.readString();
    email = in.readString();
    phone = in.readString();
    address = in.readString();
    imageUrl = in.readString();
    imageUri = in.readString();
    dob = in.readString();
    long createdAtMillis = in.readLong();
    createdAt = createdAtMillis != -1 ? new Date(createdAtMillis) : null;
    long updatedAtMillis = in.readLong();
    updatedAt = updatedAtMillis != -1 ? new Date(updatedAtMillis) : null;
  }

  public static final Creator<CustomerUiModel> CREATOR =
      new Creator<>() {
        @NonNull @Contract("_ -> new")
        @Override
        public CustomerUiModel createFromParcel(Parcel in) {
          return new CustomerUiModel(in);
        }

        @NonNull @Contract(value = "_ -> new", pure = true)
        @Override
        public CustomerUiModel[] newArray(int size) {
          return new CustomerUiModel[size];
        }
      };

  public String getId() {
    return id;
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
  public String getDob() {
    return dob;
  }

  public void setDob(String dob) {
    this.dob = dob;
    notifyPropertyChanged(BR.dob);
  }

  @Bindable
  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
    notifyPropertyChanged(BR.address);
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(@NonNull Parcel dest, int flags) {
    dest.writeString(id);
    dest.writeString(name);
    dest.writeString(email);
    dest.writeString(phone);
    dest.writeString(address);
    dest.writeString(imageUrl);
    dest.writeString(imageUri);
    dest.writeString(dob);
    dest.writeLong(createdAt != null ? createdAt.getTime() : -1);
    dest.writeLong(updatedAt != null ? updatedAt.getTime() : -1);
  }
}
