package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandFirebaseObject {

  @SerializedName("id")
  private String id;

  @SerializedName("storeId")
  private String storeId;

  @SerializedName("name")
  private String name;

  @SerializedName("productCount")
  private int productCount;

  @SerializedName("createdAt")
  private Date createdAt;

  @SerializedName("updatedAt")
  private Date updatedAt;
}
