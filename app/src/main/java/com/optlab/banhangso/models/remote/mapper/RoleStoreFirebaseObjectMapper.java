package com.optlab.banhangso.models.remote.mapper;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import java.util.List;
import java.util.stream.Collectors;

public class RoleStoreFirebaseObjectMapper {

  private RoleStoreFirebaseObjectMapper() {}

  @NonNull public static RoleStore toDomain(@NonNull RoleStoreFirebaseObject roleStoreFirebaseObject) {
    RoleStore roleStore = new RoleStore();
    roleStore.setId(roleStoreFirebaseObject.getId());
    roleStore.setName(roleStoreFirebaseObject.getName());
    roleStore.setDescription(roleStoreFirebaseObject.getDescription());
    roleStore.setImageUrl(roleStoreFirebaseObject.getImageUrl());
    roleStore.setRole(roleStoreFirebaseObject.getRole());
    roleStore.setCreatedAt(roleStoreFirebaseObject.getCreatedAt());
    roleStore.setUpdatedAt(roleStoreFirebaseObject.getUpdatedAt());
    return roleStore;
  }

  @NonNull public static List<RoleStore> toDomains(
      @NonNull List<RoleStoreFirebaseObject> roleStoreFirebaseObjects) {
    return roleStoreFirebaseObjects.stream()
        .map(RoleStoreFirebaseObjectMapper::toDomain)
        .collect(Collectors.toList());
  }
}
