package com.optlab.banhangso.repositories;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import com.optlab.banhangso.models.remote.mappers.RoleStoreFirebaseObjectMapper;
import com.optlab.banhangso.models.remote.mappers.StoreFirebaseObjectMapper;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import com.optlab.banhangso.services.interfaces.FirebaseStoreService;
import com.optlab.banhangso.services.interfaces.StoreService;
import io.reactivex.rxjava3.core.Single;
import java.util.List;
import timber.log.Timber;

public class StoreRepositoryImpl implements StoreRepository {

  private final FirebaseStoreService firebaseStoreService;
  private final StoreService storeService;
  private final ErrorHandler errorHandler;

  public StoreRepositoryImpl(
      FirebaseStoreService firebaseStoreService,
      StoreService storeService,
      ErrorHandler errorHandler) {
    this.firebaseStoreService = firebaseStoreService;
    this.storeService = storeService;
    this.errorHandler = errorHandler;
  }

  @NonNull @Override
  public Single<Result<List<RoleStore>>> getUserStores(@NonNull String userId) {
    return storeService
        .getUserStores(userId)
        .doOnSubscribe(unused -> Timber.d("Starting to fetch stores..."))
        .flatMap(
            responseObject -> {
              if (responseObject.isSuccess()) {
                List<RoleStoreFirebaseObject> roleStoreFirebaseObjects =
                    responseObject.data().stores();

                List<RoleStore> roleStores =
                    RoleStoreFirebaseObjectMapper.toDomains(roleStoreFirebaseObjects);
                Timber.d("Fetched %d stores for user %s", roleStores.size(), userId);
                return Single.just(new Result.Success<>(roleStores));
              } else {
                Throwable throwable =
                    new ApiResponseException(responseObject.message(), responseObject.code());
                return Single.just(
                    new Result.Failure<List<RoleStore>>(errorHandler.getError(throwable)));
              }
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Single<Result<Store>> getStore(@NonNull String storeId) {
    return firebaseStoreService
        .getStore(storeId)
        .flatMap(
            firebaseStoreObject -> {
              Store store = StoreFirebaseObjectMapper.toDomain(firebaseStoreObject);
              return Single.just((Result<Store>) new Result.Success<>(store));
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @NonNull @Override
  public Single<Result<String>> setStore(@NonNull String userId, @NonNull Store store) {
    StoreFirebaseObject storeFirebaseObject = StoreFirebaseObjectMapper.fromDomain(store);
    return storeService
        .setStore(userId, storeFirebaseObject)
        .flatMap(
            responseObject -> {
              if (responseObject.isSuccess()) {
                String storeId = responseObject.data().storeId();
                return Single.just(new Result.Success<>(storeId));
              } else {
                Throwable throwable =
                    new ApiResponseException(responseObject.message(), responseObject.code());
                return Single.just(new Result.Failure<String>(errorHandler.getError(throwable)));
              }
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @NonNull @Override
  public Single<Result<Void>> deleteStore(@NonNull String storeId) {
    return firebaseStoreService
        .deleteStore(storeId)
        .andThen(Single.just((Result<Void>) new Result.Success<Void>(null)))
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }
}
