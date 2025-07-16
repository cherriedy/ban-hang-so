package com.optlab.banhangso.paging.productsale;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.ProductSaleFirebaseObject;
import com.optlab.banhangso.models.remote.responses.ProductSaleResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.ProductSaleService;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

public abstract class BaseProductSalePagingSource
    extends BasePagingSource<ProductSaleFirebaseObject> {

  protected final ProductSaleService productSaleService;

  protected BaseProductSalePagingSource(
      PreferencesRepository preferencesRepository, ProductSaleService productSaleService) {
    super(preferencesRepository);
    this.productSaleService = productSaleService;
  }

  @NotNull protected LoadResult<Integer, ProductSaleFirebaseObject> mapToResult(
      @NonNull Response<ProductSaleResponse.Collection> response) {
    if (response.isFailure()) {
      Throwable throwable = new ApiResponseException(response.message(), response.code());
      return new LoadResult.Error<>(throwable);
    } else {
      ProductSaleResponse.Collection collection = response.data();
      List<ProductSaleFirebaseObject> items = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (items.isEmpty()) {
        Timber.d("No product sales found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevPage = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextPage = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d(
          "Loaded page %d of %d pages with %d product sales",
          currentPageNumber, totalPageNumber, items.size());

      return new LoadResult.Page<>(items, prevPage, nextPage);
    }
  }
}
