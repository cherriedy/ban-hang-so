package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Staff
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.StaffFirebaseObjectMapper
import com.optlab.banhangso.paging.staff.StaffPagingSource
import com.optlab.banhangso.paging.staff.StaffSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.StaffRepository
import com.optlab.banhangso.services.interfaces.StaffService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class StaffRepositoryImpl(
    private val staffService: StaffService,
    private val errorHandler: ErrorHandler,
    private val preferencesRepositoryKt: PreferencesRepositoryKt,
) : StaffRepository, PaginationRepository {
    override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

    override fun getStaffs(): Flowable<PagingData<Staff>> =
        Pager(pagingConfig) {
            StaffPagingSource(preferencesRepositoryKt, staffService)
        }.flowable.map { pagingData -> pagingData.map(StaffFirebaseObjectMapper::toDomain) }

    override fun searchStaffs(query: String): Flowable<PagingData<Staff>> =
        Pager(pagingConfig) {
            StaffSearchPagingSource(preferencesRepositoryKt, staffService, query)
        }.flowable.map { pagingData -> pagingData.map(StaffFirebaseObjectMapper::toDomain) }

    override fun getStaff(staffId: String): Single<Result<Staff>> =
        storeId.flatMap {
            staffService.getStaff(staffId, it)
        }.map { response ->
            if (response.isSuccess) {
                response.data.item.let {
                    Result.Success(StaffFirebaseObjectMapper.toDomain(it))
                }
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }

    override fun updateStaff(staff: Staff): Single<Result<Staff>> =
        storeId.flatMap { storeId ->
            StaffFirebaseObjectMapper.fromDomain(staff).let { staffFirebaseObject ->
                staffService.updateStaff(staff.id, storeId, staffFirebaseObject)
            }
        }.map { response ->
            if (response.isSuccess) {
                response.data.item.let {
                    Result.Success(StaffFirebaseObjectMapper.toDomain(it))
                }
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }

    override fun createStaff(staff: Staff): Single<Result<Void>> {
        return storeId.flatMap { storeId ->
            val staffFirebaseObject = StaffFirebaseObjectMapper.fromDomain(staff)
            staffService.createStaff(storeId, staffFirebaseObject)
        }.map { response ->
            if (response.isSuccess) {
                @Suppress("UNCHECKED_CAST")
                Result.Success(null) as Result<Void>
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

    override fun deleteStaff(staffId: String): Single<Result<Void>> =
        storeId.flatMap { storeId ->
            staffService.deleteStaff(staffId, storeId)
        }.map { response ->
            if (response.isSuccess) {
                @Suppress("UNCHECKED_CAST")
                Result.Success(null) as Result<Void>
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }
}
