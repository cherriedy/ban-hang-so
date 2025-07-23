package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Pagination;
import java.util.List;

public class RoleStoreResponse {

  public static class Collection extends Pagination<RoleStoreFirebaseObject> {
    public Collection(
        List<RoleStoreFirebaseObject> items, int total, int page, int size, int pages) {
      super(items, total, page, size, pages);
    }
  }

  public record Item(@SerializedName("item") RoleStoreFirebaseObject item) {}
}
