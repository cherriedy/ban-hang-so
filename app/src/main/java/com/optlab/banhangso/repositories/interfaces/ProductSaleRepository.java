package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.domain.ProductSale;
import io.reactivex.rxjava3.core.Flowable;

public interface ProductSaleRepository extends BaseRepository {

  @NonNull Flowable<PagingData<ProductSale>> getProductSales();

  @NonNull Flowable<PagingData<ProductSale>> searchProductSales(@NonNull String query);
}
