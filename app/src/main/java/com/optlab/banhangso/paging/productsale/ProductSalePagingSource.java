package com.optlab.banhangso.paging.productsale;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.ProductSaleFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.ProductSaleService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductSalePagingSource extends BaseProductSalePagingSource {

  public ProductSalePagingSource(
      PreferencesRepositoryKt preferencesRepository, ProductSaleService productSaleService) {
    super(preferencesRepository, productSaleService);
  }

  @NonNull @Override
  public Single<LoadResult<Integer, ProductSaleFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .flatMap(
            storeId ->
                productSaleService
                    .getSaleProducts(storeId, currentPageNumber, ITEMS_PER_PAGE)
                    .observeOn(Schedulers.io())
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
