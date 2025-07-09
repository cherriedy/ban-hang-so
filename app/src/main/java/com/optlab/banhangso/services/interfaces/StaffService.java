package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import com.optlab.banhangso.models.remote.responses.StaffResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StaffService {

  @GET("/staffs")
  Single<Response<StaffResponse.Collection>> getStaffs(
      @Query("store_id") String storeId, @Query("page") int page, @Query("size") int size);

  @GET("/staffs/{staff_id}")
  Single<Response<StaffResponse.Item>> getStaff(
      @Path("staff_id") String staffId, @Query("store_id") String storeId);

  @GET("/staffs/search")
  Single<Response<StaffResponse.Collection>> searchStaff(
      @Query("store_id") String storeId,
      @Query("page") int page,
      @Query("size") int size,
      @Query("q") String query);

  @POST("/staffs")
  Single<Response<Void>> createStaff(
      @Query("store_id") String storeId, @Body StaffFirebaseObject staffFirebaseObject);

  @PUT("/staffs/{staff_id}")
  Single<Response<StaffResponse.Item>> updateStaff(
      @Path("staff_id") String staffId,
      @Query("store_id") String storeId,
      @Body StaffFirebaseObject staffFirebaseObject);

  @DELETE("/staffs/{staff_id}")
  Single<Response<Void>> deleteStaff(
      @Path("staff_id") String staffId, @Query("store_id") String storeId);
}
