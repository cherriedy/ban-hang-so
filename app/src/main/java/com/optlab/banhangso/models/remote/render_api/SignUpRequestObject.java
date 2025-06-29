package com.optlab.banhangso.models.remote.render_api;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequestObject {

  @SerializedName("email")
  private String email;

  @SerializedName("password")
  private String password;

  @SerializedName("displayName")
  private String contactName;

  @SerializedName("role")
  private String role;

  @SerializedName("phone")
  private String phone;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("storeId")
  private String storeId;

  @SerializedName("storeInfo")
  private StoreInfo storeInfo;

  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class StoreInfo {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("imageUrl")
    private String imageUrl;
  }
}
