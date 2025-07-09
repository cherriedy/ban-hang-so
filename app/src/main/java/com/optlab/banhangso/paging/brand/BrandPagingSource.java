package com.optlab.banhangso.paging.brand;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.BrandService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BrandPagingSource extends BaseBrandPagingSource {

  public BrandPagingSource(PreferencesRepository preferencesRepository, BrandService brandService) {
    super(preferencesRepository, brandService);
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
                    .getBrands(storeId, currentPageNumber, ITEMS_PER_PAGE)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
