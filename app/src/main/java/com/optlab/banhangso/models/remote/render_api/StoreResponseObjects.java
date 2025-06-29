package com.optlab.banhangso.models.remote.render_api;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import java.util.List;

public class StoreResponseObjects {
  public record CreateStoreResponse(@SerializedName("store_id") String storeId) {}

  public record UserStoresResponse(
      @SerializedName("stores") List<RoleStoreFirebaseObject> stores) {}
}
