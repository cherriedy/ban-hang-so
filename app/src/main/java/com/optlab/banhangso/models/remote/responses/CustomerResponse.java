package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class CustomerResponse {

  public static class Collection extends Pagination<CustomerFirebaseObject> {
    public Collection(
        List<CustomerFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") CustomerFirebaseObject item) {}
}
