package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Customer
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.CustomerFirebaseObjectMapper
import com.optlab.banhangso.paging.customer.CustomerPagingSource
import com.optlab.banhangso.paging.customer.CustomerSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.CustomerRepository
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.services.interfaces.CustomerService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class CustomerRepositoryImpl(
    private val customerService: CustomerService,
    private val errorHandler: ErrorHandler,
    private val preferencesRepositoryKt: PreferencesRepositoryKt,
) : CustomerRepository, PaginationRepository {
    override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

    override fun getCustomers(): Flowable<PagingData<Customer>> =
        Pager(pagingConfig) {
            CustomerPagingSource(preferencesRepositoryKt, customerService)
        }.flowable.map { pagingData -> pagingData.map(CustomerFirebaseObjectMapper::toDomain) }

    override fun getCustomer(customerId: String): Single<Result<Customer>> {
        return storeId.flatMap { storeId ->
            customerService.getCustomer(customerId, storeId)
        }.map { response ->
            if (response.isSuccess) {
                response.data.item.let { customerFirebaseObject ->
                    CustomerFirebaseObjectMapper.toDomain(customerFirebaseObject).let { customer ->
                        Result.Success(customer)
                    }
                }
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

    override fun searchCustomers(query: String): Flowable<PagingData<Customer>> =
        Pager(pagingConfig) {
            CustomerSearchPagingSource(preferencesRepositoryKt, customerService, query)
        }.flowable.map { pagingData -> pagingData.map(CustomerFirebaseObjectMapper::toDomain) }

    override fun updateCustomer(customer: Customer): Single<Result<Void>> =
        storeId.flatMap { storeId ->
            CustomerFirebaseObjectMapper.fromDomain(customer).let { customerFirebaseObject ->
                customerService.updateCustomer(
                    customer.id,
                    storeId,
                    customerFirebaseObject,
                )
            }
        }.map { response ->
            if (response.isSuccess) {
                Result.Success<Void>(null) as Result<Void>
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure<Void>(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }

    override fun deleteCustomer(customerId: String): Single<Result<Void>> {
        return storeId.flatMap { storeId ->
            customerService.deleteCustomer(customerId, storeId)
        }.map { response ->
            if (response.isSuccess) {
                Result.Success<Void>(null) as Result<Void>
            } else {
                ApiResponseException(response.message, response.code).let {
                    Result.Failure<Void>(errorHandler.getError(it))
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }

    override fun createCustomer(customer: Customer): Single<Result<Void>> {
        return storeId.flatMap { storeId ->
            CustomerFirebaseObjectMapper.fromDomain(customer).let { customerFirebaseObject ->
                customerService.createCustomer(storeId, customerFirebaseObject)
            }.map { response ->
                if (response.isSuccess) {
                    Result.Success<Void>(null) as Result<Void>
                } else {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure<Void>(errorHandler.getError(it))
                    }
                }
            }
        }.onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }
}
