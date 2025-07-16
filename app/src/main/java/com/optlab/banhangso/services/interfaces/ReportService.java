package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.ReportSummaryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ReportService {

  @GET("reports/summary")
  Single<Response<ReportSummaryFirebaseObject>> getSummary(@Query("store_id") String storeId);
}
