package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Product;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface ProductRepository {

  Flowable<PagingData<Product>> getProducts();

  Single<Result<Product>> getProduct(@NonNull String productId);

  Flowable<PagingData<Product>> searchProduct(@NonNull String query);

  Single<Result<Product>> createProduct(@NonNull Product product);

  Single<Result<Product>> updateProduct(@NonNull Product product);

  Single<Result<Boolean>> deleteProduct(@NonNull String productId);
}
