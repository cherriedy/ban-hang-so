package com.optlab.banhangso.features.main.staff.viewmodels

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.features.main.staff.models.StaffUiModel
import com.optlab.banhangso.features.main.staff.models.mappers.StaffUiModelMapper
import com.optlab.banhangso.repositories.interfaces.StaffRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class StaffListViewModel
@Inject
constructor(
    private val staffRepository: StaffRepository,
) : ViewModel() {
    private val staffs: Flowable<PagingData<StaffUiModel>>
    private val searchQuery: ObservableField<String> = ObservableField("")
    private val searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

    init {
        staffs =
            searchProcessor.distinctUntilChanged().doOnNext { Timber.d("Staff search query: $it") }
                .switchMap { query ->
                    if (query.isBlank()) {
                        return@switchMap staffRepository.staffs.map { pagingData ->
                            pagingData.map(StaffUiModelMapper::fromDomain)
                        }.cachedIn(viewModelScope)
                    } else {
                        return@switchMap staffRepository.searchStaffs(query).map { pagingData ->
                            pagingData.map(StaffUiModelMapper::fromDomain)
                        }.cachedIn(viewModelScope)
                    }
                }

        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        searchQuery.addOnPropertyChangedCallback(
            object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(
                    sender: Observable?,
                    propertyId: Int,
                ) {
                    @Suppress("UNCHECKED_CAST")
                    val query =
                        (sender as ObservableField<String?>).get()
                    searchProcessor.onNext(query ?: "")
                }
            },
        )
    }

    fun getStaffs() = staffs

    fun getSearchQuery() = searchQuery
}
