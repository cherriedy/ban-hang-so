package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.Brand
import com.optlab.banhangso.models.exceptions.ApiResponseException
import com.optlab.banhangso.models.remote.mappers.BrandFirebaseObjectMapper
import com.optlab.banhangso.paging.brand.BrandPagingSource
import com.optlab.banhangso.paging.brand.BrandSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.BrandRepository
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.services.interfaces.BrandService
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class BrandRepositoryImpl
    @Inject
    constructor(
        private val preferencesRepositoryKt: PreferencesRepositoryKt,
        private val brandService: BrandService,
        private val errorHandler: ErrorHandler,
    ) : BrandRepository, PaginationRepository {
        /**
         * Provides access to the preferences repository instance.
         *
         * @return The PreferencesRepository instance used by this repository
         */
        override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

        /**
         * Retrieves a paginated list of all brands for the current store.
         *
         * @return A Flowable stream of PagingData containing Brand domain objects
         */
        override fun getBrands(): Flowable<PagingData<Brand>> =
            Pager(pagingConfig) { BrandPagingSource(preferencesRepositoryKt, brandService) }
                .flowable
                .map { pagingData -> pagingData.map(BrandFirebaseObjectMapper::toDomain) }

        /**
         * Retrieves a specific brand by its ID.
         *
         * @param brandId The unique identifier of the brand to retrieve
         * @return A Single containing a Result with either the Brand object or an error
         */
        override fun getBrand(brandId: String): Single<Result<Brand>> =
            storeId
                .flatMap { storeId -> brandService.getBrand(brandId, storeId) }
                .map { response ->
                    if (response.isSuccess) {
                        BrandFirebaseObjectMapper.toDomain(response.data.item).let { Result.Success(it) }
                    } else {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    }
                }
                .onErrorReturn { Result.Failure(errorHandler.getError(it)) }

        /**
         * Searches for brands based on a query string with pagination support.
         *
         * @param query The search term to filter brands
         * @return A Flowable stream of PagingData containing matching Brand domain objects
         */
        override fun searchBrands(query: String): Flowable<PagingData<Brand>> =
            Pager(pagingConfig) { BrandSearchPagingSource(preferencesRepositoryKt, brandService, query) }
                .flowable
                .map { pagingData -> pagingData.map(BrandFirebaseObjectMapper::toDomain) }

        /**
         * Updates an existing brand with new information.
         *
         * @param brand The Brand object containing updated data
         * @return A Single containing a Result indicating success or failure of the update operation
         */
        override fun updateBrand(brand: Brand): Single<Result<Void>> =
            storeId
                .flatMap { storeId ->
                    BrandFirebaseObjectMapper.fromDomain(brand).let { brandFirebaseObject ->
                        brandService.updateBrand(brand.id, storeId, brandFirebaseObject)
                    }
                }
                .map { response ->
                    if (response.isSuccess) {
                        Result.Success<Void>(null) as Result<Void>
                    } else {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure<Void>(errorHandler.getError(it))
                        }
                    }
                }
                .onErrorReturn { Result.Failure(errorHandler.getError(it)) }

        /**
         * Creates a new brand in the store.
         *
         * @param brand The Brand object to be created
         * @return A Single containing a Result indicating success or failure of the creation operation
         */
        override fun createBrand(brand: Brand): Single<Result<Void>> =
            storeId.flatMap { storeId ->
                BrandFirebaseObjectMapper.fromDomain(brand)
                    .let { brandFirebaseObject -> brandService.createBrand(storeId, brandFirebaseObject) }
                    .map { response ->
                        if (response.isSuccess) {
                            Result.Success<Void>(null) as Result<Void>
                        } else {
                            ApiResponseException(response.message, response.code).let {
                                Result.Failure<Void>(errorHandler.getError(it))
                            }
                        }
                    }
                    .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
            }

        /**
         * Deletes a brand by its ID from the store.
         *
         * @param brandId The unique identifier of the brand to delete
         * @return A Single containing a Result indicating success or failure of the deletion operation
         */
        override fun deleteBrand(brandId: String): Single<Result<Void>> =
            storeId
                .flatMap { storeId -> brandService.deleteBrand(brandId, storeId) }
                .map { response ->
                    if (response.isSuccess) {
                        Result.Success<Void>(null) as Result<Void>
                    } else {
                        ApiResponseException(response.message, response.code).let {
                            Result.Failure(errorHandler.getError(it))
                        }
                    }
                }
                .onErrorReturn { Result.Failure(errorHandler.getError(it)) }
    }
