package com.optlab.banhangso.paging.product;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.ProductService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductSearchPagingSource extends BaseProductPagingSource {

  private final String query;

  public ProductSearchPagingSource(
      PreferencesRepositoryKt preferencesRepository, ProductService productService, String query) {
    super(preferencesRepository, productService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, ProductFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .subscribeOn(Schedulers.io())
        .flatMap(
            store ->
                productService
                    .searchProducts(store, currentPageNumber, ITEMS_PER_PAGE, query)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
