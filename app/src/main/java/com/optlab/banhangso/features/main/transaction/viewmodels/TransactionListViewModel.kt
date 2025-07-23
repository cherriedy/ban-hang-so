package com.optlab.banhangso.features.main.transaction.viewmodels

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import com.optlab.banhangso.features.main.transaction.models.TransactionSummaryUiModel
import com.optlab.banhangso.features.main.transaction.models.mappers.TransactionSummaryUiModelMapper
import com.optlab.banhangso.models.application.FilterParams
import com.optlab.banhangso.repositories.interfaces.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TransactionListViewModel
    @Inject
    constructor(private val transactionRepository: TransactionRepository) : ViewModel() {
        private val searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")
        private val filterProcessor: BehaviorProcessor<FilterParams> =
            BehaviorProcessor.createDefault(FilterParams())

        private val _searchQuery: ObservableField<String> = ObservableField()
        val searchQuery: ObservableField<String> = _searchQuery

        init {
            _searchQuery.addOnPropertyChangedCallback(
                object : Observable.OnPropertyChangedCallback() {
                    override fun onPropertyChanged(
                        sender: Observable?,
                        propertyId: Int,
                    ) {
                        @Suppress("UNCHECKED_CAST")
                        val query: String? = (sender as ObservableField<String>).get()
                        searchProcessor.onNext(query ?: "")
                    }
                },
            )
        }

        private val _transactions: Flowable<PagingData<TransactionSummaryUiModel>> =
            Flowable.combineLatest(
                searchProcessor.distinctUntilChanged(),
                filterProcessor.distinctUntilChanged(),
            ) { searchQuery, filterParams ->
                Pair(searchQuery, filterParams)
            }
                .doOnNext { (searchQuery, filterParams) ->
                    Timber.d("Search/Filter changed: query='$searchQuery', filter=$filterParams")
                }
                .switchMap { (searchQuery, filterParams) ->
                    if (searchQuery.isNotBlank()) {
                        // Use search endpoint when search query is active
                        transactionRepository.searchTransactions(searchQuery).map { pagingData ->
                            pagingData.map(TransactionSummaryUiModelMapper::fromDomain)
                        }
                    } else {
                        // Use filter endpoint when no search query
                        transactionRepository.getTransactions(filterParams).map { pagingData ->
                            pagingData.map(TransactionSummaryUiModelMapper::fromDomain)
                        }
                    }
                }

        val transactions: Flowable<PagingData<TransactionSummaryUiModel>>
            get() = _transactions

        fun setFilterParams(filterParams: FilterParams) = filterProcessor.onNext(filterParams)
    }
