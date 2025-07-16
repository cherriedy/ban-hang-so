package com.optlab.banhangso.repositories

import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.ReportSummary
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.ReportSummaryFirebaseObjectMapper
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository
import com.optlab.banhangso.repositories.interfaces.ReportRepository
import com.optlab.banhangso.services.interfaces.ReportService
import io.reactivex.rxjava3.core.Single

class ReportRepositoryImpl(
    private val reportService: ReportService,
    private val errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
) : ReportRepository {
    override fun getPreferencesRepository(): PreferencesRepository = preferencesRepository

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
}
