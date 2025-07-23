package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.ReportSummary;
import com.optlab.banhangso.models.domain.SaleReport;
import io.reactivex.rxjava3.core.Single;

public interface ReportRepository extends BaseRepository {
  @NonNull Single<Result<ReportSummary>> getSummary();

  @NonNull Single<Result<SaleReport>> getSaleReport(@NonNull String startDate, @NonNull String endDate);
}
