package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class StaffResponse {

  public static class Collection extends Pagination<StaffFirebaseObject> {
    public Collection(List<StaffFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") StaffFirebaseObject item) {}
}
