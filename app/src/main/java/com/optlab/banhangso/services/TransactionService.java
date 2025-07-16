package com.optlab.banhangso.services;

import com.optlab.banhangso.models.domain.Cart;
import com.optlab.banhangso.models.remote.responses.TransactionResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import java.util.Map;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface TransactionService {

  @GET("transactions")
  Single<Response<TransactionResponse.Collection>> getTransactions(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @QueryMap Map<String, Object> queryParams);

  @GET("transactions/search")
  Single<Response<TransactionResponse.Collection>> searchTransactions(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @GET("transactions/{transaction_id}")
  Single<Response<TransactionResponse.Item>> getTransaction(
      @Path("transaction_id") String transactionId, @Query("store_id") String storeId);

  @POST("transactions")
  Single<Response<TransactionResponse.Item>> setTransaction(
      @Query("store_id") String storeId, @Body Cart cart);
}
