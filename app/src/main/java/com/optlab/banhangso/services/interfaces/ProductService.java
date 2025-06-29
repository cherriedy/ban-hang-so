package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.ProductResponseObject;
import com.optlab.banhangso.models.remote.render_api.ResponseObject;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService {

  @GET("products")
  Single<ResponseObject<ProductResponseObject.ProductCollection>> getProducts(
      @Query("page") int page, @Query("size") int size);

  @POST("products")
  Single<ResponseObject<ProductResponseObject.ProductItem>> createProduct(
      @Body ProductFirebaseObject productFirebaseObject);

  @GET("products/{product_id}")
  Single<ResponseObject<ProductResponseObject.ProductItem>> getProduct(
      @Path("product_id") String productId);

  @GET("products/search")
  Single<ResponseObject<ProductResponseObject.ProductCollection>> searchProducts(
      @Query("page") int page, @Query("size") int size, @Query("q") String query);

  @PUT("products/{product_id}")
  Single<ResponseObject<ProductResponseObject.ProductItem>> updateProduct(
      @Path("product_id") String productId, @Body ProductFirebaseObject productFirebaseObject);

  @DELETE("products/{product_id}")
  Single<ResponseObject<Boolean>> deleteProduct(@Path("product_id") String productId);
}
