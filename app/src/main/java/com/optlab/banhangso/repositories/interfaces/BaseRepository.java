package com.optlab.banhangso.repositories.interfaces;

import android.util.Pair;
import io.reactivex.rxjava3.core.Single;
import org.jetbrains.annotations.NotNull;

public interface BaseRepository {

  @NotNull PreferencesRepositoryKt getPreferencesRepositoryKt();

  @NotNull default Single<String> getStoreId() {
    return getPreferencesRepositoryKt()
        .getStoreRx()
        .filter(store -> !store.isEmpty()) // Filter out empty stores
        .take(1) // Take the first non-empty store
        .singleOrError()
        .map(
            store -> {
              if (store.isEmpty()) {
                throw new IllegalStateException("Store is not set in preferences");
              } else {
                return store.getId();
              }
            });
  }

  @NotNull default Single<String> getUserId() {
    return getPreferencesRepositoryKt()
        .getUserRx()
        .filter(user -> !user.isEmpty()) // Filter out empty users
        .take(1) // Take the first non-empty user
        .singleOrError()
        .map(
            user -> {
              if (user.isEmpty()) {
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
