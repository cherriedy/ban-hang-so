package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.RenderResponseObject;
import com.optlab.banhangso.models.remote.render_api.StoreResponseObjects;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RenderStoreService {
    @GET("stores/user/{userId}")
    Single<RenderResponseObject<StoreResponseObjects.UserStoresResponse>> getUserStores(
            @Path("userId") String userId);

    @POST("stores/user/{userId}")
    Single<RenderResponseObject<StoreResponseObjects.CreateStoreResponse>> setStore(
            @Path("userId") String userId, @Body StoreFirebaseObject storeFirebaseObject);
}
