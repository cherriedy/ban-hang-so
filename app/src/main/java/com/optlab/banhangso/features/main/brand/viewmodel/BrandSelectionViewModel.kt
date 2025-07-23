package com.optlab.banhangso.features.main.brand.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.features.main.brand.models.BrandUiModel
import com.optlab.banhangso.features.main.brand.models.mappers.BrandUiModelMapper
import com.optlab.banhangso.repositories.interfaces.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

@HiltViewModel
class BrandSelectionViewModel
    @Inject
    constructor(private val brandRepository: BrandRepository) :
    ViewModel() {
        private val _brands: Flowable<PagingData<BrandUiModel>>
        val brands: Flowable<PagingData<BrandUiModel>>
            get() = _brands

        init {
            @Suppress("OPT_IN_USAGE")
            _brands =
                brandRepository.brands
                    .map { pagingData -> pagingData.map(BrandUiModelMapper::fromDomain) }
                    .cachedIn(viewModelScope)
        }
    }
