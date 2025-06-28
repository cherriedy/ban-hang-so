package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.ProductResponseObject;
import com.optlab.banhangso.models.remote.render_api.RenderResponseObject;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RenderProductService {

    @GET("products")
    Single<RenderResponseObject<ProductResponseObject.ProductCollection>> getProducts(
            @Query("page") int page, @Query("size") int size);

    @POST("products")
    Single<RenderResponseObject<ProductResponseObject.ProductItem>> createProduct(
            @Body ProductFirebaseObject productFirebaseObject);

    @GET("products/{product_id}")
    Single<RenderResponseObject<ProductResponseObject.ProductItem>> getProduct(
            @Path("product_id") String productId);

    @GET("products/search")
    Single<RenderResponseObject<ProductResponseObject.ProductCollection>> searchProducts(
            @Query("page") int page, @Query("size") int size, @Query("q") String query);

    @PUT("products/{product_id}")
    Single<RenderResponseObject<ProductResponseObject.ProductItem>> updateProduct(
            @Path("product_id") String productId,
            @Body ProductFirebaseObject productFirebaseObject);

    @DELETE("products/{product_id}")
    Single<RenderResponseObject<Boolean>> deleteProduct(@Path("product_id") String productId);
}
