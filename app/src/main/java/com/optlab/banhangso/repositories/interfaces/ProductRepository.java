package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Product;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface ProductRepository extends BaseRepository {

  @NonNull Flowable<PagingData<Product>> getProducts();

  @NonNull Single<Result<Product>> getProduct(@NonNull String productId);

  @NonNull Flowable<PagingData<Product>> searchProduct(@NonNull String query);

  @NonNull Single<Result<Void>> createProduct(@NonNull Product product);

  @NonNull Single<Result<Void>> updateProduct(@NonNull Product product);

  @NonNull Single<Result<Void>> deleteProduct(@NonNull String productId);
}
