package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import com.optlab.banhangso.models.remote.responses.CustomerResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CustomerService {

  @GET("customers/")
  Single<Response<CustomerResponse.Collection>> getCustomers(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @GET("customers/{customer_id}")
  Single<Response<CustomerResponse.Item>> getCustomer(
      @Path("customer_id") String customerId, @Query("store_id") String storeId);

  @GET("customers/search")
  Single<Response<CustomerResponse.Collection>> searchCustomers(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @POST("customers")
  Single<Response<Void>> createCustomer(
      @Query("store_id") String storeId, @Body CustomerFirebaseObject customerFirebaseObject);

  @PUT("customers/{customer_id}")
  Single<Response<Void>> updateCustomer(
      @Path("customer_id") String customerId,
      @Query("store_id") String storeId,
      @Body CustomerFirebaseObject customerFirebaseObject);

  @DELETE("customers/{customer_id}")
  Single<Response<Void>> deleteCustomer(
      @Path("customer_id") String customerId, @Query("store_id") String storeId);
}
