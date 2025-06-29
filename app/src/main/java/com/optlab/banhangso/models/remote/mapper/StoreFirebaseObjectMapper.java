package com.optlab.banhangso.models.remote.mapper;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import java.util.List;
import java.util.stream.Collectors;

public class StoreFirebaseObjectMapper {

  private StoreFirebaseObjectMapper() {}

  @NonNull public static Store toDomain(@NonNull StoreFirebaseObject storeFirebaseObject) {
    Store store = new Store();
    store.setId(storeFirebaseObject.getId());
    store.setName(storeFirebaseObject.getName());
    store.setDescription(storeFirebaseObject.getDescription());
    store.setImageUrl(storeFirebaseObject.getImageUrl());
    store.setCreatedAt(storeFirebaseObject.getCreatedAt());
    store.setUpdatedAt(storeFirebaseObject.getUpdatedAt());
    return store;
  }

  @NonNull public static List<Store> toDomains(@NonNull List<StoreFirebaseObject> storeFirebaseObjects) {
    return storeFirebaseObjects.stream()
        .map(StoreFirebaseObjectMapper::toDomain)
        .collect(Collectors.toList());
  }

  @NonNull public static StoreFirebaseObject fromDomain(@NonNull Store store) {
    StoreFirebaseObject storeFirebaseObject = new StoreFirebaseObject();
    storeFirebaseObject.setId(store.getId());
    storeFirebaseObject.setName(store.getName());
    storeFirebaseObject.setDescription(store.getDescription());
    storeFirebaseObject.setImageUrl(store.getImageUrl());
    storeFirebaseObject.setCreatedAt(store.getCreatedAt());
    storeFirebaseObject.setUpdatedAt(store.getUpdatedAt());
    return storeFirebaseObject;
  }
}
