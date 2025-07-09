package com.optlab.banhangso.features.main.customer.viewmodels

import androidx.databinding.Observable
import androidx.databinding.ObservableField
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
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CustomerListViewModel @Inject constructor(private val customerRepository: CustomerRepository) :
    ViewModel() {
    private val _customers: Flowable<PagingData<CustomerUiModel>>
    val customers: Flowable<PagingData<CustomerUiModel>>
        get() = _customers

    private val _searchQuery: ObservableField<String> = ObservableField<String>("")
    val searchQuery: ObservableField<String>
        get() = _searchQuery

    private val searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

    init {
        @OptIn(ExperimentalCoroutinesApi::class)
        _customers = searchProcessor.distinctUntilChanged()
            .doOnNext { Timber.d("Customer search query {$it} ") }.switchMap { query ->
                if (query.isBlank()) {
                    customerRepository.customers
                } else {
                    customerRepository.searchCustomers(query)
                }.map { pagingData ->
                    pagingData.map(CustomerUiModelMappers::fromDomain)
                }.cachedIn(viewModelScope)
            }

        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        _searchQuery.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(
                    sender: Observable?,
                    propertyId: Int,
                ) {
                    @Suppress("UNCHECKED_CAST")
                    val query: String? =
                        (sender as ObservableField<String>).get()
                    searchProcessor.onNext(query ?: "")
                }
            },
        )
    }
}
