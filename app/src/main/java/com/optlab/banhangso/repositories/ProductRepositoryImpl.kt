package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Product
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.ProductFirebaseObjectMapper
import com.optlab.banhangso.paging.product.ProductPagingSource
import com.optlab.banhangso.paging.product.ProductSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.ProductRepository
import com.optlab.banhangso.services.interfaces.ProductService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

class ProductRepositoryImpl(
    private val productService: ProductService,
    private val preferencesRepositoryKt: PreferencesRepositoryKt,
    private val errorHandler: ErrorHandler,
) : ProductRepository, PaginationRepository {
    override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

    override fun getProducts(): Flowable<PagingData<Product>> =
        Pager(pagingConfig) { ProductPagingSource(preferencesRepositoryKt, productService) }
            .flowable
            .map { pagingData -> pagingData.map(ProductFirebaseObjectMapper::toDomain) }

    override fun getProduct(productId: String): Single<Result<Product>> =
        storeId
            .flatMap { productService.getProduct(productId, it) }
            .map { response ->
                if (response.isSuccess) {
                    response.data.item.let {
                        Result.Success<Product>(ProductFirebaseObjectMapper.toDomain(it))
                    }
                } else {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure(errorHandler.getError(it))
                    }
                }
            }
            .onErrorReturn { error -> Result.Failure(errorHandler.getError(error)) }

    override fun searchProduct(query: String): Flowable<PagingData<Product>> =
        Pager(pagingConfig) { ProductSearchPagingSource(preferencesRepositoryKt, productService, query) }
            .flowable
            .map { pagingData -> pagingData.map(ProductFirebaseObjectMapper::toDomain) }

    override fun createProduct(product: Product): Single<Result<Void>> =
        ProductFirebaseObjectMapper.fromDomain(product).let { productFirebaseObject ->
            storeId
                .flatMap { productService.createProduct(it, productFirebaseObject) }
                .map { renderResponseObject ->
                    if (renderResponseObject.isSuccess) {
                        Result.Success<Void>(null)
                    } else {
                        ApiResponseException(renderResponseObject.message, renderResponseObject.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    }
                }
                .onErrorReturn { error -> Result.Failure(errorHandler.getError(error)) }
        }

    override fun updateProduct(product: Product): Single<Result<Void>> =
        ProductFirebaseObjectMapper.fromDomain(product).let { productFirebaseObject ->
            storeId
                .flatMap { productService.updateProduct(product.id, it, productFirebaseObject) }
                .map { response ->
                    if (response.isSuccess) {
                        Result.Success<Void>(null)
                    } else {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    }
                }
                .onErrorReturn { error -> Result.Failure(errorHandler.getError(error)) }
        }

    override fun deleteProduct(productId: String): Single<Result<Void>> =
        storeId
            .flatMap { productService.deleteProduct(productId, it) }
            .map { response ->
                if (response.isSuccess) {
                    Result.Success<Void>(null)
                } else {
                    ApiResponseException(response.message, response.code).let {
                        Result.Failure(errorHandler.getError(it))
                    }
                }
            }
            .onErrorReturn { error -> Result.Failure(errorHandler.getError(error)) }
}
