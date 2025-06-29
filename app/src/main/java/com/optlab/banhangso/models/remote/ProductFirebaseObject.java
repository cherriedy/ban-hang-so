package com.optlab.banhangso.models.remote;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class ProductFirebaseObject {
  @SerializedName("id")
  private String id;

  @SerializedName("barcode")
  private String barcode;

  @SerializedName("category")
  private Category category;

  @SerializedName("brand")
  private Brand brand;

  @SerializedName("name")
  private String name;

  @SerializedName("purchasePrice")
  private double purchasePrice;

  @SerializedName("sellingPrice")
  private double sellingPrice;

  @SerializedName("avatarUrl")
  private String avatarUrl;

  @SerializedName("stockQuantity")
  private int stockQuantity;

  @SerializedName("description")
  private String description;

  @SerializedName("status")
  private boolean status;

  @SerializedName("discountPrice")
  private double discountPrice;

  @SerializedName("note")
  private String note;

  @ServerTimestamp
  @SerializedName("createdAt")
  private Date createdAt;

  @ServerTimestamp
  @SerializedName("updatedAt")
  private Date updatedAt;
}
