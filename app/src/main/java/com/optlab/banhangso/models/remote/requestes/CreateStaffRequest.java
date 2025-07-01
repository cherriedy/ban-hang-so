package com.optlab.banhangso.models.remote.requestes;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateStaffRequest {

  @SerializedName("email")
  private String email;

  @SerializedName("displayName")
  private String name;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("phone")
  private String phone;
}
