package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreFirebaseObject {
  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  @SerializedName("description")
  private String description;

  @SerializedName("imageUrl")
  private String imageUrl;

  @SerializedName("createdAt")
  private Date createdAt;

  @SerializedName("updatedAt")
  private Date updatedAt;
}
