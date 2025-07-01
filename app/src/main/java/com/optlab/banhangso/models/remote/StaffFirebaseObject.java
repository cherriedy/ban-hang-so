package com.optlab.banhangso.models.remote;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class StaffFirebaseObject {

  @SerializedName("id")
  private String id;

  @SerializedName("storeId")
  private String storeId;

  @SerializedName("email")
  private String email;

  @SerializedName("phone")
  private String phone;

  @SerializedName("displayName")
  private String name;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("active")
  private Boolean active;

  @SerializedName("role")
  private String role;

  @SerializedName("createdAt")
  @ServerTimestamp
  private Date createdAt;

  @SerializedName("updatedAt")
  @ServerTimestamp
  private Date updatedAt;

  @Exclude
  public String getId() {
    return id;
  }

  @Exclude
  public String getStoreId() {
    return storeId;
  }
}
