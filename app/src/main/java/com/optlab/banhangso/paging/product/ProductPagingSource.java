package com.optlab.banhangso.paging.product;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.ProductService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductPagingSource extends BaseProductPagingSource {

  public ProductPagingSource(
      PreferencesRepository preferencesRepository, ProductService productService) {
    super(preferencesRepository, productService);
  }

  /**
   * Core method responsible for loading a page of data. This is called by the Paging library when
   * it needs to load more data.
   *
   * @param loadParams Contains parameters for the load operation including the page key
   * @return A Single that emits a LoadResult containing the loaded data
   */
  @NonNull @Override
  public Single<LoadResult<Integer, ProductFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .flatMap(
            storeId ->
                productService
                    .getProducts(storeId, currentPageNumber, ITEMS_PER_PAGE)
                    .subscribeOn(Schedulers.io())
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
