package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class BrandResponse {

  public static class Collection extends Pagination<BrandFirebaseObject> {
    public Collection(List<BrandFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") BrandFirebaseObject item) {}
}
