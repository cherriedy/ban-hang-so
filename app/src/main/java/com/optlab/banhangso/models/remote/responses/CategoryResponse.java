package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import java.util.List;

public class CategoryResponse {
  public record CategoryCollection(
      @SerializedName("items") List<CategoryFirebaseObject> items,
      @SerializedName("total") int total,
      @SerializedName("page") int page,
      @SerializedName("size") int size,
      @SerializedName("pages") int pages) {}

  public record CategoryItem(@SerializedName("item") CategoryFirebaseObject item) {}
}
