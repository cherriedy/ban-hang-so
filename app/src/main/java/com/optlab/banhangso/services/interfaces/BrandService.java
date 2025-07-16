package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import com.optlab.banhangso.models.remote.responses.BrandResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BrandService {

  @GET("brands")
  Single<Response<BrandResponse.Collection>> getBrands(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @POST("brands")
  Single<Response<BrandResponse.Item>> createBrand(
      @Query("store_id") String storeId, @Body BrandFirebaseObject brandFirebaseObject);

  @GET("brands/{brand_id}")
  Single<Response<BrandResponse.Item>> getBrand(
      @Path("brand_id") String brandId, @Query("store_id") String storeId);

  @GET("brands/search")
  Single<Response<BrandResponse.Collection>> searchCategories(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @PUT("brands/{brand_id}")
  Single<Response<BrandResponse.Item>> updateBrand(
      @Path("brand_id") String brandId,
      @Query("store_id") String storeId,
      @Body BrandFirebaseObject brandFirebaseObject);

  @DELETE("brands/{brand_id}")
  Single<Response<Void>> deleteBrand(
      @Path("brand_id") String brandId, @Query("store_id") String storeId);
}
