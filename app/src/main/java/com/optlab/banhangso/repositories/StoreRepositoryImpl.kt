package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.store.RoleStore
import com.optlab.banhangso.models.domain.store.Store
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.RoleStoreFirebaseObjectMapper
import com.optlab.banhangso.models.remote.mappers.StoreFirebaseObjectMapper
import com.optlab.banhangso.paging.store.RoleStorePagingSource
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.StoreRepository
import com.optlab.banhangso.services.interfaces.StoreService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class StoreRepositoryImpl(
    private val preferencesRepositoryKt: PreferencesRepositoryKt,
    private val storeService: StoreService,
    private val errorHandler: ErrorHandler,
) : StoreRepository, PaginationRepository {
    override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

    override fun getUserStores(): Flowable<PagingData<RoleStore>> =
        Pager(pagingConfig) { RoleStorePagingSource(preferencesRepositoryKt, storeService) }
            .flowable
            .map { pagingData -> pagingData.map(RoleStoreFirebaseObjectMapper::toDomain) }

    override fun getStore(): Single<Result<Store>> =
        storeId.flatMap { storeId ->
            storeService
                .getUserStore(storeId)
                .map { response ->
                    if (response.isFailure) {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    } else {
                        StoreFirebaseObjectMapper.toDomain(response.data.item).let {
                            Result.Success(it)
                        }
                    }
                }
                .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
        }

    override fun setStore(store: Store): Single<Result<Void>> =
        StoreFirebaseObjectMapper.fromDomain(store)
            .let {
                storeService.setStore(it).map { response ->
                    if (response.isFailure) {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    } else {
                        Result.Success<Void>(null)
                    }
                }
            }
            .onErrorReturn { Result.Failure(errorHandler.getError(it)) }

    override fun updateStore(store: Store): Single<Result<Void>> =
        storeId.flatMap { storeId ->
            StoreFirebaseObjectMapper.fromDomain(store).let { storeFirebaseObject ->
                storeService
                    .updateStore(storeId, storeFirebaseObject)
                    .map { response ->
                        if (response.isFailure) {
                            ApiResponseException(response.message, response.code).let {
                                Result.Failure(errorHandler.getError(it))
                            }
                        } else {
                            Result.Success<Void>(null)
                        }
                    }
                    .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
            }
        }

    override fun deleteStore(storeId: String): Single<Result<Void>> =
        storeService
            .deleteStore(storeId)
            .map { response ->
                if (response.isFailure) {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure(errorHandler.getError(it))
                    }
                } else {
                    Result.Success<Void>(null)
                }
            }
            .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
}
