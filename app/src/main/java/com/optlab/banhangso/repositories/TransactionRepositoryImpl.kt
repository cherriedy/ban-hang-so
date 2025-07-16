package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.FilterParams
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Cart
import com.optlab.banhangso.models.domain.TransactionRecord
import com.optlab.banhangso.models.domain.TransactionSummary
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.TransactionRecordFirebaseObjectMapper
import com.optlab.banhangso.models.remote.mappers.TransactionSummaryFirebaseObjectMapper
import com.optlab.banhangso.paging.transaction.TransactionFiltersPagingSource
import com.optlab.banhangso.paging.transaction.TransactionSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository
import com.optlab.banhangso.repositories.interfaces.TransactionRepository
import com.optlab.banhangso.services.TransactionService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

class TransactionRepositoryImpl(
    private val preferencesRepository: PreferencesRepository,
    private val transactionService: TransactionService,
    private val errorHandler: ErrorHandler,
) : TransactionRepository, PaginationRepository {
    override fun getPreferencesRepository(): PreferencesRepository = preferencesRepository

    override fun getTransactions(filterParams: FilterParams): Flowable<PagingData<TransactionSummary>> =
        Pager(pagingConfig) {
            TransactionFiltersPagingSource(
                preferencesRepository,
                transactionService,
                filterParams,
            )
        }
            .flowable
            .map { pagingData -> pagingData.map(TransactionSummaryFirebaseObjectMapper::toDomain) }

    override fun searchTransactions(query: String): Flowable<PagingData<TransactionSummary>> =
        Pager(pagingConfig) {
            TransactionSearchPagingSource(preferencesRepository, transactionService, query)
        }
            .flowable
            .map { pagingData -> pagingData.map(TransactionSummaryFirebaseObjectMapper::toDomain) }

    override fun getTransaction(transactionId: String): Single<Result<TransactionRecord>> =
        storeId
            .flatMap { storeId -> transactionService.getTransaction(transactionId, storeId) }
            .map { response ->
                if (response.isError) {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure<TransactionRecord>(errorHandler.getError(it))
                    }
                } else {
                    TransactionRecordFirebaseObjectMapper.toDomain(response.data.item).let {
                        Timber.d("getTransaction: $it")
                        Result.Success<TransactionRecord>(it)
                    }
                }
            }
            .onErrorReturn { Result.Failure(errorHandler.getError(it)) }

    override fun setTransaction(cart: Cart): Single<Result<TransactionRecord>> =
        storeUserIdPair
            .flatMap { pair ->
                val storeId = pair.first
                val userId = pair.second
                cart
                    .apply { this.staffId = userId }
                    .let { transactionService.setTransaction(storeId, it) }
            }
            .map { response ->
                if (response.isError) {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure<TransactionRecord>(errorHandler.getError(it))
                    }
                } else {
                    val item = response.data.item
                    if (item != null) {
                        val transactionRecord = TransactionRecordFirebaseObjectMapper.toDomain(item)
                        Result.Success(transactionRecord)
                    } else {
                        Result.Failure<TransactionRecord>(
                            errorHandler.getError(Exception("Failed to create transaction")),
                        )
                    }
                }
            }
            .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
}
