package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import com.optlab.banhangso.models.remote.responses.RoleStoreResponse;
import com.optlab.banhangso.models.remote.responses.StoreResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StoreService {
  @GET("stores/user/{user_id}")
  Single<Response<RoleStoreResponse.Collection>> getUserStores(
      @Path("user_id") String user_id, @Query("page") int page, @Query("size") int size);

  @POST("stores/user")
  Single<Response<StoreResponse.CreateStoreResponse>> setStore(
      @Body StoreFirebaseObject storeFirebaseObject);

  @GET("stores/{store_id}")
  Single<Response<StoreResponse.Item>> getUserStore(@Path("store_id") String store_id);

  @PUT("stores/{store_id}")
  Single<Response<Void>> updateStore(
      @Path("store_id") String storeId, @Body StoreFirebaseObject storeFirebaseObject);

  @DELETE("stores/{store_id}")
  Single<Response<Void>> deleteStore(@Path("store_id") String store_id);
}
