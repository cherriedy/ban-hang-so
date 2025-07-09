package com.optlab.banhangso.paging.category;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.CategoryResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.CategoryService;
import java.util.List;
import org.jetbrains.annotations.Contract;
import timber.log.Timber;

public abstract class BaseCategoryPagingSource extends BasePagingSource<CategoryFirebaseObject> {

  public final CategoryService categoryService;

  protected BaseCategoryPagingSource(
          PreferencesRepository preferencesRepository, CategoryService categoryService) {
    super(preferencesRepository);
    this.categoryService = categoryService;
  }

  @NonNull @Contract("_ -> new")
  protected LoadResult<Integer, CategoryFirebaseObject> mapToResult(
      @NonNull Response<CategoryResponse.Collection> categoryCollectionResponse) {
    if (categoryCollectionResponse.isFailure()) {
      Throwable throwable =
          new ApiResponseException(
              categoryCollectionResponse.message(), categoryCollectionResponse.code());
      return new LoadResult.Error<>(throwable);
    } else {
      CategoryResponse.Collection collection = categoryCollectionResponse.data();
      List<CategoryFirebaseObject> items = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (items == null || items.isEmpty()) {
        Timber.w("No customers found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevKey = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextKey = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d("Loaded page %d with %d items", currentPageNumber, items.size());

      return new LoadResult.Page<>(items, prevKey, nextKey);
    }
  }
}
