package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import com.optlab.banhangso.models.remote.responses.StoreResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface StoreService {
  @GET("stores/user/{userId}")
  Single<Response<StoreResponse.UserStoresResponse>> getUserStores(@Path("userId") String userId);

  @POST("stores/user/{userId}")
  Single<Response<StoreResponse.CreateStoreResponse>> setStore(
      @Path("userId") String userId, @Body StoreFirebaseObject storeFirebaseObject);
}
