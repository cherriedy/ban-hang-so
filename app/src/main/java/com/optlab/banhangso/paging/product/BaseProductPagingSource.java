package com.optlab.banhangso.paging.product;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.responses.ProductResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.ProductService;

import org.jetbrains.annotations.Contract;

import java.util.List;

import timber.log.Timber;

public abstract class BaseProductPagingSource extends BasePagingSource<ProductFirebaseObject> {

  protected final ProductService productService;
  protected final PreferencesRepositoryKt preferencesRepositoryKt;

  protected BaseProductPagingSource(
          PreferencesRepositoryKt preferencesRepositoryKt, ProductService productService) {
    super(preferencesRepositoryKt);
    this.productService = productService;
    this.preferencesRepositoryKt = preferencesRepositoryKt;
  }

  @NonNull @Contract("_ -> new")
  protected LoadResult<Integer, ProductFirebaseObject> mapToResult(
      @NonNull Response<ProductResponse.Collection> productCollectionResponse) {
    if (productCollectionResponse.isError()) {
      Throwable throwable =
          new ApiResponseException(
              productCollectionResponse.message(), productCollectionResponse.code());
      return new LoadResult.Error<>(throwable);
    } else {
      ProductResponse.Collection collection = productCollectionResponse.data();
      List<ProductFirebaseObject> productFirebaseObjects = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

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
}
