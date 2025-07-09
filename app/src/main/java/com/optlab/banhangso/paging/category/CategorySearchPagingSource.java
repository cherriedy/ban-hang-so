package com.optlab.banhangso.paging.category;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.CategoryService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CategorySearchPagingSource extends BaseCategoryPagingSource {

  @NonNull private final String query;

  public CategorySearchPagingSource(
      PreferencesRepository preferencesRepository,
      CategoryService categoryService,
      @NonNull String query) {
    super(preferencesRepository, categoryService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, CategoryFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .subscribeOn(Schedulers.io())
        .flatMap(
            storeId ->
                categoryService
                    .searchCategories(storeId, currentPageNumber, ITEMS_PER_PAGE, query)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
