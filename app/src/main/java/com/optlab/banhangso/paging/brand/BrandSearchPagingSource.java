package com.optlab.banhangso.paging.brand;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.BrandService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BrandSearchPagingSource extends BaseBrandPagingSource {

  private final String query;

  public BrandSearchPagingSource(
      PreferencesRepositoryKt preferencesRepository, BrandService brandService, String query) {
    super(preferencesRepository, brandService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, BrandFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .subscribeOn(Schedulers.io())
        .flatMap(
            storeId ->
                brandService
                    .searchCategories(storeId, currentPageNumber, ITEMS_PER_PAGE, query)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
