package com.optlab.banhangso.features.main.store.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.store.models.StoreUiModel;
import com.optlab.banhangso.models.domain.store.Store;
import java.util.List;
import java.util.stream.Collectors;

public class StoreUiModelMapper {

    private StoreUiModelMapper() {}

    @NonNull public static StoreUiModel fromDomain(@NonNull Store store) {
        StoreUiModel storeUiModel = new StoreUiModel();
        storeUiModel.setId(store.getId());
        storeUiModel.setName(store.getName());
        storeUiModel.setDescription(store.getDescription());
        storeUiModel.setImageUrl(store.getImageUrl());
        storeUiModel.setCreatedAt(store.getCreatedAt());
        storeUiModel.setUpdatedAt(store.getUpdatedAt());
        return storeUiModel;
    }

    @NonNull public static List<StoreUiModel> fromDomains(@NonNull List<Store> stores) {
        return stores.stream().map(StoreUiModelMapper::fromDomain).collect(Collectors.toList());
    }

    @NonNull public static Store toDomain(@NonNull StoreUiModel storeUiModel) {
        Store store = new Store();
        store.setId(storeUiModel.getId());
        store.setName(storeUiModel.getName());
        store.setDescription(storeUiModel.getDescription());
        store.setImageUrl(storeUiModel.getImageUrl());
        store.setCreatedAt(storeUiModel.getCreatedAt());
        store.setUpdatedAt(storeUiModel.getUpdatedAt());
        return store;
    }
}
