package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class CategoryResponse {

  public static class Collection extends Pagination<CategoryFirebaseObject> {
    public Collection(
        List<CategoryFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") CategoryFirebaseObject item) {}
}
