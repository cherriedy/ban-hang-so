package com.optlab.banhangso.repositories.interfaces;

import android.util.Pair;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

public interface BaseRepository {

  @NotNull PreferencesRepository getPreferencesRepository();

  @NotNull default Single<String> getStoreId() {
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

  @NotNull default Single<String> getUserId() {
    return getPreferencesRepository()
        .getUser()
        .switchIfEmpty(Single.just(User.empty()))
        .map(
            user -> {
              if (user == null || user.isEmpty()) {
                throw new IllegalStateException("User is not set in preferences");
              } else {
                return user.getId();
              }
            });
  }

  @NotNull default Single<Pair<String, String>> getStoreUserIdPair() {
    return Single.zip(getStoreId(), getUserId(), Pair::new);
  }
}
