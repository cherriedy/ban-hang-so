package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.CategoryResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CategoryService {

  @GET("categories")
  Single<Response<CategoryResponse.Collection>> getCategories(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @POST("categories")
  Single<Response<CategoryResponse.Item>> createCategory(
      @Query("store_id") String storeId, @Body CategoryFirebaseObject categoryFirebaseObject);

  @GET("categories/{category_id}")
  Single<Response<CategoryResponse.Item>> getCategory(
      @Path("category_id") String categoryId, @Query("store_id") String storeId);

  @GET("categories/search")
  Single<Response<CategoryResponse.Collection>> searchCategories(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @PUT("categories/{category_id}")
  Single<Response<CategoryResponse.Item>> updateCategory(
      @Path("category_id") String categoryId,
      @Query("store_id") String storeId,
      @Body CategoryFirebaseObject categoryFirebaseObject);

  @DELETE("categories/{category_id}")
  Single<Response<Void>> deleteCategory(
      @Path("category_id") String categoryId, @Query("store_id") String storeId);
}
