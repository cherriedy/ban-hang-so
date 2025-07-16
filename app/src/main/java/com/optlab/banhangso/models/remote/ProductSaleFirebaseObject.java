package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSaleFirebaseObject {

  @SerializedName("id")
  private String id;

  @SerializedName("name")
  private String name;

  @SerializedName("thumbnailUrl")
  private String thumbnailUrl;

  @SerializedName("sellingPrice")
  private double sellingPrice;

  @SerializedName("discountPrice")
  private double discountPrice;

  @SerializedName("purchasePrice")
  private double purchasePrice;

  @SerializedName("status")
  private boolean status;
}
