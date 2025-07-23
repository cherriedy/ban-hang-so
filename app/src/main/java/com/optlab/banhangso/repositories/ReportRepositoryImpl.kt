package com.optlab.banhangso.repositories

import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.ReportSummary
import com.optlab.banhangso.models.domain.SaleReport
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.ReportSummaryFirebaseObjectMapper
import com.optlab.banhangso.models.remote.mappers.SaleReportDtoMapper
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.ReportRepository
import com.optlab.banhangso.services.interfaces.ReportService
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

class ReportRepositoryImpl(
    private val reportService: ReportService,
    private val errorHandler: ErrorHandler,
    private val preferencesRepositoryKt: PreferencesRepositoryKt,
) : ReportRepository {
    override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

    override fun getSummary(): Single<Result<ReportSummary>> {
        return storeId
            .flatMap { storeId -> reportService.getSummary(storeId) }
            .map { response ->
                if (response.isError) {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure(errorHandler.getError(it))
                    }
                } else {
                    ReportSummaryFirebaseObjectMapper.toDomain(response.data).let {
                        Result.Success(it)
                    }
                }
            }
            .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

    override fun getSaleReport(
        startDate: String,
        endDate: String,
    ): Single<Result<SaleReport>> =
        storeId
            .flatMap { storeId ->
                reportService.getSaleReport(storeId, startDate, endDate).map { response ->
                    if (response.isFailure) {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    } else {
                        SaleReportDtoMapper.toDomain(response.data).let { Result.Success(it) }
                    }
                }
            }
            .onErrorReturn {
                Timber.e(it, "There was an error fetching the sale report ${it.message}")
                Result.Failure(errorHandler.getError(it))
            }
}
