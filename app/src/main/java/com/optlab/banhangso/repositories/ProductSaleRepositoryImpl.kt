package com.optlab.banhangso.repositories

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.flowable
import com.optlab.banhangso.models.domain.ProductSale
import com.optlab.banhangso.models.remote.mappers.ProductSaleFirebaseObjectMapper
import com.optlab.banhangso.paging.productsale.ProductSalePagingSource
import com.optlab.banhangso.paging.productsale.ProductSaleSearchPagingSource
import com.optlab.banhangso.repositories.interfaces.PaginationRepository
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.ProductSaleRepository
import com.optlab.banhangso.services.interfaces.ProductSaleService
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

class ProductSaleRepositoryImpl
    @Inject
    constructor(
        private val preferencesRepositoryKt: PreferencesRepositoryKt,
        private val productSaleService: ProductSaleService,
    ) : ProductSaleRepository, PaginationRepository {
        override fun getPreferencesRepositoryKt(): PreferencesRepositoryKt = preferencesRepositoryKt

        override fun getProductSales(): Flowable<PagingData<ProductSale>> =
            Pager(pagingConfig) { ProductSalePagingSource(preferencesRepositoryKt, productSaleService) }
                .flowable
                .map { pagingData -> pagingData.map(ProductSaleFirebaseObjectMapper::toDomain) }

        override fun searchProductSales(query: String): Flowable<PagingData<ProductSale>> =
            Pager(pagingConfig) { ProductSaleSearchPagingSource(preferencesRepositoryKt, productSaleService, query) }
                .flowable
                .map { pagingData -> pagingData.map(ProductSaleFirebaseObjectMapper::toDomain) }
    }
