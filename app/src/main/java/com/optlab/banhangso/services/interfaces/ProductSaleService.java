package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.responses.ProductSaleResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductSaleService {

  @GET("sales/products")
  Single<Response<ProductSaleResponse.Collection>> getSaleProducts(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @GET("sales/products/search")
  Single<Response<ProductSaleResponse.Collection>> searchSaleProducts(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);
}
