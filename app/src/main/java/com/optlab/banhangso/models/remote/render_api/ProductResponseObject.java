package com.optlab.banhangso.models.remote.render_api;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import java.util.List;

public class ProductResponseObject {
  public record ProductCollection(
      @SerializedName("items") List<ProductFirebaseObject> items,
      @SerializedName("total") int total,
      @SerializedName("page") int page,
      @SerializedName("size") int size,
      @SerializedName("pages") int pages) {}

  public record ProductItem(@SerializedName("item") ProductFirebaseObject item) {}
}
