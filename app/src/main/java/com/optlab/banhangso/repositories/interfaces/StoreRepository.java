package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.models.domain.store.Store;
import io.reactivex.rxjava3.core.Single;
import java.util.List;

/**
 * Interface for Store Repository operations Provides methods to interact with store data from both
 * local database and remote sources
 */
public interface StoreRepository {

  @NonNull Single<Result<List<RoleStore>>> getUserStores(@NonNull String userId);

  Single<Result<Store>> getStore(@NonNull String storeId);

  @NonNull Single<Result<String>> setStore(@NonNull String userId, @NonNull Store store);

  @NonNull Single<Result<Void>> deleteStore(@NonNull String storeId);
}
