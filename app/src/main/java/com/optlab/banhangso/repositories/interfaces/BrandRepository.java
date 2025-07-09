package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;

import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Brand;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface BrandRepository extends BaseRepository {

  @NonNull
  Flowable<PagingData<Brand>> getBrands();

  @NonNull
  Single<Result<Brand>> getBrand(@NonNull String brandId);

  @NonNull Flowable<PagingData<Brand>> searchBrands(@NonNull String query);

  @NonNull Single<Result<Void>> updateBrand(@NonNull Brand brand);

  @NonNull Single<Result<Void>> createBrand(@NonNull Brand brand);

  @NonNull Single<Result<Void>> deleteBrand(@NonNull String brandId);
}
