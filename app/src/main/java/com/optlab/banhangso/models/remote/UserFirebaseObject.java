package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFirebaseObject {

  private String id;

  @SerializedName("contactName")
  private String contactName;

  @SerializedName("phone")
  private String phone;

  @SerializedName("email")
  private String email;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("stores")
  private List<Store> stores;

  @SerializedName("createdAt")
  private Date createdAt;

  @SerializedName("updatedAt")
  private Date updatedAt;

  public String getId() {
    return id;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Store {

    @SerializedName("id")
    private String id;

    @SerializedName("role")
    private String role;
  }
}
