package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class ProductResponse {

  public static class Collection extends Pagination<ProductFirebaseObject> {
    public Collection(List<ProductFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") ProductFirebaseObject item) {}
}
