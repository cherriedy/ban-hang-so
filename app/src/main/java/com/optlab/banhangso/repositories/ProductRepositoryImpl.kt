package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.Constants.ITEMS_PER_PAGE
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Product
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mapper.ProductFirebaseObjectMapper
import com.optlab.banhangso.pagingsource.ProductPagingSource
import com.optlab.banhangso.pagingsource.ProductSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.ProductRepository
import com.optlab.banhangso.services.interfaces.RenderProductService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber

class ProductRepositoryImpl(
    private val renderProductService: RenderProductService,
    private val errorHandler: ErrorHandler,
) : ProductRepository {
    val pagingConfig =
        PagingConfig(
            pageSize = ITEMS_PER_PAGE,
            prefetchDistance = ITEMS_PER_PAGE,
            enablePlaceholders = false,
            initialLoadSize = ITEMS_PER_PAGE * 3,
        )

    override fun getProducts(): Flowable<PagingData<Product>> {
        return Pager(pagingConfig) {
            ProductPagingSource(renderProductService)
        }.flowable.map { pagingData -> pagingData.map(ProductFirebaseObjectMapper::toDomain) }
    }

    override fun getProduct(productId: String): Single<Result<Product>> =
        renderProductService.getProduct(productId).map { renderResponseObject ->
            if (renderResponseObject.isSuccess) {
                renderResponseObject.data.item.let {
                    Result.Success<Product>(ProductFirebaseObjectMapper.toDomain(it))
                }
            } else {
                ApiResponseException(
                    renderResponseObject.message,
                    renderResponseObject.code,
                ).let { Result.Failure<Product>(errorHandler.getError(it)) }
            }
        }.onErrorReturn { error -> Result.Failure<Product>(errorHandler.getError(error)) }

    override fun searchProduct(query: String): Flowable<PagingData<Product>> {
        return Pager(pagingConfig) {
            ProductSearchPagingSource(query, renderProductService)
        }.flowable.map { pagingData -> pagingData.map(ProductFirebaseObjectMapper::toDomain) }
    }

    override fun createProduct(product: Product): Single<Result<Product>> =
        ProductFirebaseObjectMapper.fromDomain(product).let { productFirebaseObject ->
            renderProductService.createProduct(productFirebaseObject).map { renderResponseObject ->
                Timber.d("Creating product response: $renderResponseObject")
                if (renderResponseObject.isSuccess) {
                    renderResponseObject.data.item.let {
                        Result.Success<Product>(ProductFirebaseObjectMapper.toDomain(it))
                    }
                } else {
                    ApiResponseException(
                        renderResponseObject.message,
                        renderResponseObject.code,
                    ).let {
                        Result.Failure<Product>(errorHandler.getError(it))
                    }
                }
            }.onErrorReturn { error -> Result.Failure<Product>(errorHandler.getError(error)) }
        }

    override fun updateProduct(product: Product): Single<Result<Product>> =
        ProductFirebaseObjectMapper.fromDomain(product).let { productFirebaseObject ->
            renderProductService.updateProduct(product.id, productFirebaseObject).map { renderResponseObject ->
                if (renderResponseObject.isSuccess) {
                    renderResponseObject.data.item.let {
                        Result.Success<Product>(ProductFirebaseObjectMapper.toDomain(it))
                    }
                } else {
                    ApiResponseException(
                        renderResponseObject.message,
                        renderResponseObject.code,
                    ).let {
                        Result.Failure<Product>(errorHandler.getError(it))
                    }
                }
            }
        }.onErrorReturn { error -> Result.Failure<Product>(errorHandler.getError(error)) }

    override fun deleteProduct(productId: String): Single<Result<Boolean>> =
        renderProductService.deleteProduct(productId).map { renderResponseObject ->
            Result.Success<Boolean>(renderResponseObject.data) as Result<Boolean>
        }.onErrorReturn { error -> Result.Failure<Boolean>(errorHandler.getError(error)) }
}
