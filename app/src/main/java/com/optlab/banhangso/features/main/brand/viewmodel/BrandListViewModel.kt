package com.optlab.banhangso.features.main.brand.viewmodel

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.R
import com.optlab.banhangso.features.main.brand.models.BrandUiModel
import com.optlab.banhangso.features.main.brand.models.mappers.BrandUiModelMapper
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel
import com.optlab.banhangso.models.application.AppError
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.repositories.interfaces.BrandRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

@HiltViewModel
class BrandListViewModel @Inject constructor(private val brandRepository: BrandRepository) :
  RxViewModel() {

  private val _searchQuery: ObservableField<String> = ObservableField()
  val searchQuery: ObservableField<String>
    get() = _searchQuery

  private val _searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

  @OptIn(ExperimentalCoroutinesApi::class)
  private val _brands: Flowable<PagingData<BrandUiModel>> =
    _searchProcessor
      .distinctUntilChanged()
      .doOnNext { Timber.d("Brand search query $it") }
      .switchMap { query ->
        if (query.isBlank()) {
          brandRepository.brands
        } else {
          brandRepository.searchBrands(query)
        }
      }
      .map { pagingData -> pagingData.map(BrandUiModelMapper::fromDomain) }
      .cachedIn(viewModelScope)

  val brands: Flowable<PagingData<BrandUiModel>>
    get() = _brands

  private val _deletionCompleted: MutableLiveData<Boolean> = MutableLiveData()
  val deletionCompleted
    get() = _deletionCompleted

  init {
    _searchQuery.addOnPropertyChangedCallback(
      object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
          @Suppress("UNCHECKED_CAST") val query: String? = (sender as ObservableField<String>).get()
          _searchProcessor.onNext(query ?: "")
        }
      }
    )
  }

  fun deleteBrand(brandId: String) {
    val disposable =
      brandRepository
        .deleteBrand(brandId)
        .subscribeOn(Schedulers.io())
        .doOnSubscribe {
          isLoading.postValue(true)
          deletionCompleted.postValue(false)
        }
        .observeOn(AndroidSchedulers.mainThread())
        .doFinally {
          isLoading.value = false
          deletionCompleted.value = true
        }
        .subscribe(this::onDeleteBrandSuccess, this::onDeleteBrandError)

    disposables.add(disposable)
  }

  private fun onDeleteBrandSuccess(result: Result<Void>) {
    messageResId.value =
      when (result) {
        is Result.Success<Void> -> {
          R.string.notify_delete_brand_success
        }

        is Result.Failure<Void> -> {
          result.error.let {
            when (it) {
              is AppError.ForbiddenError -> R.string.error_forbidden
              is AppError.NetServiceError -> R.string.error_network
              is AppError.NotFoundError -> R.string.error_brand_not_found
              else -> R.string.error_unknown
            }
          }
        }
      }
  }

  private fun onDeleteBrandError(throwable: Throwable) {
    messageResId.value = R.string.error_unknown
    Timber.e(throwable, "There was an error deleting the brand: %s", throwable.message)
  }
}
