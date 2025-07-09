package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.responses.ProductResponse;
import com.optlab.banhangso.models.remote.responses.UploadResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductService extends ImageUploadService {

  @GET("products")
  Single<Response<ProductResponse.Collection>> getProducts(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @POST("products")
  Single<Response<Void>> createProduct(
      @Query("store_id") String storeId, @Body ProductFirebaseObject productFirebaseObject);

  @GET("products/{product_id}")
  Single<Response<ProductResponse.Item>> getProduct(
      @Path("product_id") String productId, @Query("store_id") String storeId);

  @GET("products/search")
  Single<Response<ProductResponse.Collection>> searchProducts(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @PUT("products/{product_id}")
  Single<Response<Void>> updateProduct(
      @Path("product_id") String productId,
      @Query("store_id") String storeId,
      @Body ProductFirebaseObject productFirebaseObject);

  @DELETE("products/{product_id}")
  Single<Response<Void>> deleteProduct(
      @Path("product_id") String productId, @Query("store_id") String storeId);

  @Multipart
  @POST("products/upload-image")
  Single<Response<UploadResponse>> uploadImage(@Part MultipartBody.Part file);
}
