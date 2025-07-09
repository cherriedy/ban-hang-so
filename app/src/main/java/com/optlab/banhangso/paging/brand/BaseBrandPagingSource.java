package com.optlab.banhangso.paging.brand;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import com.optlab.banhangso.models.remote.responses.BrandResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.BrandService;
import java.util.List;
import timber.log.Timber;

public abstract class BaseBrandPagingSource extends BasePagingSource<BrandFirebaseObject> {

  protected final BrandService brandService;

  protected BaseBrandPagingSource(
      PreferencesRepository preferencesRepository, BrandService brandService) {
    super(preferencesRepository);
    this.brandService = brandService;
  }

  @NonNull protected LoadResult<Integer, BrandFirebaseObject> mapToResult(
      @NonNull Response<BrandResponse.Collection> response) {
    if (response.isFailure()) {
      Throwable throwable = new ApiResponseException(response.message(), response.code());
      return new LoadResult.Error<>(throwable);
    }

    BrandResponse.Collection collection = response.data();
    List<BrandFirebaseObject> items = collection.getItems();

    int currentPageNumber = collection.getPage();
    int totalPageNumber = collection.getPages();

    if (items.isEmpty()) {
      Timber.d("No brands found for page %d", currentPageNumber);
      return new LoadResult.Page<>(List.of(), null, null);
    }

    Integer prevPage = currentPageNumber > 1 ? currentPageNumber - 1 : null;
    Integer nextPage = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

    return new LoadResult.Page<>(items, prevPage, nextPage);
  }
}
