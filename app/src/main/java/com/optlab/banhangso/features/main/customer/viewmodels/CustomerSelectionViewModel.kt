package com.optlab.banhangso.features.main.customer.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel
import com.optlab.banhangso.features.main.customer.models.mappers.CustomerUiModelMappers
import com.optlab.banhangso.repositories.interfaces.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import javax.inject.Inject

@HiltViewModel
class CustomerSelectionViewModel
@Inject
constructor(private val customerRepository: CustomerRepository) : ViewModel() {

    @Suppress("OPT_IN_USAGE")
    private val _customers: Flowable<PagingData<CustomerUiModel>> =
        customerRepository.customers
            .map { pagingData -> pagingData.map(CustomerUiModelMappers::fromDomain) }
            .cachedIn(viewModelScope)

    val customers: Flowable<PagingData<CustomerUiModel>>
        get() = _customers
}
