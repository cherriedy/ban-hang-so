package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.models.domain.store.Store;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface StoreRepository extends BaseRepository {

  @NonNull Flowable<PagingData<RoleStore>> getUserStores();

  @NonNull Single<Result<Store>> getStore();

  @NonNull Single<Result<Void>> setStore(@NonNull Store store);

  @NonNull Single<Result<Void>> updateStore(@NonNull Store store);

  @NonNull Single<Result<Void>> deleteStore(@NonNull String storeId);
}
