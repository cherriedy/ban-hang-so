package com.optlab.banhangso.features.main.store.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel;
import com.optlab.banhangso.models.domain.store.RoleStore;
import java.util.List;
import java.util.stream.Collectors;

public class RoleStoreUiModelMapper {

  private RoleStoreUiModelMapper() {}

  @NonNull public static RoleStoreUiModel fromDomain(@NonNull RoleStore roleStore) {
    RoleStoreUiModel roleStoreUiModel = new RoleStoreUiModel();
    roleStoreUiModel.setId(roleStore.getId());
    roleStoreUiModel.setRole(roleStore.getRole());
    roleStoreUiModel.setName(roleStore.getName());
    roleStoreUiModel.setDescription(roleStore.getDescription());
    roleStoreUiModel.setImageUrl(roleStore.getImageUrl());
    roleStoreUiModel.setCreatedAt(roleStore.getCreatedAt());
    roleStoreUiModel.setUpdatedAt(roleStore.getUpdatedAt());
    return roleStoreUiModel;
  }

  @NonNull public static List<RoleStoreUiModel> fromDomains(@NonNull List<RoleStore> roleStores) {
    return roleStores.stream().map(RoleStoreUiModelMapper::fromDomain).collect(Collectors.toList());
  }

  @NonNull public static RoleStore toDomain(@NonNull RoleStoreUiModel roleStoreUiModel) {
    RoleStore roleStore = new RoleStore();
    roleStore.setId(roleStoreUiModel.getId());
    roleStore.setRole(roleStoreUiModel.getRole());
    roleStore.setName(roleStoreUiModel.getName());
    roleStore.setDescription(roleStoreUiModel.getDescription());
    roleStore.setImageUrl(roleStoreUiModel.getImageUrl());
    roleStore.setCreatedAt(roleStoreUiModel.getCreatedAt());
    roleStore.setUpdatedAt(roleStoreUiModel.getUpdatedAt());
    return roleStore;
  }
}
