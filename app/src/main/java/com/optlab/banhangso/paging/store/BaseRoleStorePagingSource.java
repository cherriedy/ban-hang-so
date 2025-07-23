package com.optlab.banhangso.paging.store;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.RoleStoreFirebaseObject;
import com.optlab.banhangso.models.remote.responses.RoleStoreResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.StoreService;
import java.util.List;
import timber.log.Timber;

public abstract class BaseRoleStorePagingSource extends BasePagingSource<RoleStoreFirebaseObject> {

  protected final StoreService storeService;

  protected BaseRoleStorePagingSource(
      PreferencesRepositoryKt preferencesRepository, StoreService storeService) {
    super(preferencesRepository);
    this.storeService = storeService;
  }

  @NonNull protected LoadResult<Integer, RoleStoreFirebaseObject> mapToResult(
      @NonNull Response<RoleStoreResponse.Collection> roleStoreCollectionResponse) {
    if (roleStoreCollectionResponse.isFailure()) {
      Throwable throwable =
          new ApiResponseException(
              roleStoreCollectionResponse.message(), roleStoreCollectionResponse.code());
      return new LoadResult.Error<>(throwable);
    } else {
      RoleStoreResponse.Collection collection = roleStoreCollectionResponse.data();
      List<RoleStoreFirebaseObject> items = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (items.isEmpty()) {
        Timber.d("No items found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Timber.d(
          "Fetched %d items for page %d of %d", items.size(), currentPageNumber, totalPageNumber);
      Integer prevPage = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextPage = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;
      return new LoadResult.Page<>(items, prevPage, nextPage);
    }
  }
}
