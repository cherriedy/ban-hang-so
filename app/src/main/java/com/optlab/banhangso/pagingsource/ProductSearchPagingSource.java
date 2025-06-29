package com.optlab.banhangso.pagingsource;

import static com.optlab.banhangso.internal.utilities.Constants.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.ProductResponseObject;
import com.optlab.banhangso.models.remote.render_api.ResponseObject;
import com.optlab.banhangso.services.interfaces.ProductService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class ProductSearchPagingSource extends RxPagingSource<Integer, ProductFirebaseObject> {

  private final String query;
  private final ProductService productService;

  @Inject
  public ProductSearchPagingSource(@NonNull String query, ProductService productService) {
    this.query = query;
    this.productService = productService;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, ProductFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return productService
        .searchProducts(currentPageNumber, ITEMS_PER_PAGE, query)
        .subscribeOn(Schedulers.io())
        .map(this::mapToResult)
        .onErrorReturn(LoadResult.Error::new);
  }

  private LoadResult<Integer, ProductFirebaseObject> mapToResult(
      ResponseObject<ProductResponseObject.ProductCollection> productCollectionResponseObject) {
    if (productCollectionResponseObject.isError()) {
      Throwable throwable =
          new ApiResponseException(
              productCollectionResponseObject.message(), productCollectionResponseObject.code());
      return new LoadResult.Error<>(throwable);
    } else {
      ProductResponseObject.ProductCollection productCollection =
          productCollectionResponseObject.data();
      List<ProductFirebaseObject> productFirebaseObjects = productCollection.items();

      int currentPageNumber = productCollection.page();
      int totalPageNumber = productCollection.pages();

      if (productFirebaseObjects.isEmpty()) {
        Timber.w("No products found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevPageNumber = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextPageNumber = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d(
          "Loaded page %d of %d pages with %d products",
          currentPageNumber, totalPageNumber, productFirebaseObjects.size());

      return new LoadResult.Page<>(productFirebaseObjects, prevPageNumber, nextPageNumber);
    }
  }

  @Nullable @Override
  public Integer getRefreshKey(@NonNull PagingState<Integer, ProductFirebaseObject> pagingState) {
    Integer anchorPosition = pagingState.getAnchorPosition();
    if (anchorPosition == null) {
      return null;
    }

    LoadResult.Page<Integer, ProductFirebaseObject> anchorPage =
        pagingState.closestPageToPosition(anchorPosition);
    if (anchorPage == null) {
      return null;
    }

    Integer prevKey = anchorPage.getPrevKey();
    Integer nextKey = anchorPage.getNextKey();

    if (prevKey != null && nextKey != null) {
      return Math.min(prevKey + 1, nextKey - 1);
    }

    // If we only have a next key, we're at the start, return 1
    if (prevKey == null && nextKey != null) {
      return 1;
    }

    // If we only have a prev key, return that as the refresh point
    if (prevKey != null) {
      return prevKey + 1;
    }

    // If we have no keys, start from page 1
    return 1;
  }
}
