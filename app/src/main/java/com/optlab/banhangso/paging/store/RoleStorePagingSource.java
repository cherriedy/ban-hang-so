package com.optlab.banhangso.paging.store;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.StoreService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RoleStorePagingSource extends BaseRoleStorePagingSource {

  public RoleStorePagingSource(
      PreferencesRepositoryKt preferencesRepository, StoreService storeService) {
    super(preferencesRepository, storeService);
  }

  @NonNull @Override
  public Single<LoadResult<Integer, RoleStoreFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;
    return getUserId()
        .subscribeOn(Schedulers.io())
        .flatMap(
            userId ->
                storeService
                    .getUserStores(userId, currentPageNumber, ITEMS_PER_PAGE)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
