package com.optlab.banhangso.models.remote.responses;

import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.models.remote.StoreFirebaseObject;

public class StoreResponse {
  public record CreateStoreResponse(@SerializedName("store_id") String storeId) {}

  public record Item(@SerializedName("item") StoreFirebaseObject item) {}
}
