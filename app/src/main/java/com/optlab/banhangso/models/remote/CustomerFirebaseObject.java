package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerFirebaseObject {

  @SerializedName("id")
  private String id;

  @SerializedName("storeId")
  private String storeId;

  @SerializedName("name")
  private String name;

  @SerializedName("phone")
  private String phone;

  @SerializedName("email")
  private String email;

  @SerializedName("address")
  private String address;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("dob")
  private String dob;

  @SerializedName("createdAt")
  private Date createdAt;

  @SerializedName("updatedAt")
  private Date updatedAt;
}
