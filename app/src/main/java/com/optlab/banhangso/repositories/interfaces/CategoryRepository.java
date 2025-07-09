package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Category;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface CategoryRepository extends BaseRepository {
  @NonNull Flowable<PagingData<Category>> getCategories();

  @NonNull Single<Result<Category>> getCategory(@NonNull String categoryId);

  @NonNull Flowable<PagingData<Category>> searchCategories(@NonNull String query);

  @NonNull Single<Result<Void>> updateCategory(@NonNull Category category);

  @NonNull Single<Result<Void>> createCategory(@NonNull Category category);

  @NonNull Single<Result<Void>> deleteCategory(@NonNull String categoryId);
}
