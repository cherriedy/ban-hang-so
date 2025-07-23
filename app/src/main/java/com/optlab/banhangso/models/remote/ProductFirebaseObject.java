package com.optlab.banhangso.models.remote;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFirebaseObject {
  @SerializedName("id")
  private String id;

  @SerializedName("storeId")
  private String storeId;

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

  @SerializedName("thumbnailUrl")
  private String thumbnailUrl;

  @SerializedName("imageUrls")
  private List<String> imageUrls;

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

  @SerializedName("createdAt")
  private Date createdAt;

  @SerializedName("updatedAt")
  private Date updatedAt;
}
