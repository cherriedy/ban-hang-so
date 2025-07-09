package com.optlab.banhangso.repositories.interfaces;

import com.optlab.banhangso.models.domain.store.RoleStore;
import io.reactivex.rxjava3.core.Single;

public interface BaseRepository {

  PreferencesRepository getPreferencesRepository();

  default Single<String> getStoreId() {
    return getPreferencesRepository()
        .getStore()
        .switchIfEmpty(Single.just(RoleStore.empty()))
        .map(
            store -> {
              if (store == null || store.isEmpty()) {
                throw new IllegalStateException("Store is not set in preferences");
              } else {
                return store.getId();
              }
            });
  }
}
