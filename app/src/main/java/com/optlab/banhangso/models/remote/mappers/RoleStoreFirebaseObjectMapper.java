package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class RoleStoreFirebaseObjectMapper {

  @NonNull public static RoleStore toDomain(@NonNull RoleStoreFirebaseObject roleStoreFirebaseObject) {
    Store store =
        new Store(
            roleStoreFirebaseObject.getId(),
            roleStoreFirebaseObject.getName(),
            roleStoreFirebaseObject.getDescription(),
            roleStoreFirebaseObject.getImageUrl(),
            roleStoreFirebaseObject.getCreatedAt(),
            roleStoreFirebaseObject.getUpdatedAt());

    return new RoleStore(store, roleStoreFirebaseObject.getRole());
  }

  @NonNull public static List<RoleStore> toDomains(
      @NonNull List<RoleStoreFirebaseObject> roleStoreFirebaseObjects) {
    return roleStoreFirebaseObjects.stream()
        .map(RoleStoreFirebaseObjectMapper::toDomain)
        .collect(Collectors.toList());
  }
}
