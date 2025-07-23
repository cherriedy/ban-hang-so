package com.optlab.banhangso.features.main.category.viewmodel

import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagingData
import androidx.paging.map
import com.optlab.banhangso.R
import com.optlab.banhangso.features.main.category.models.CategoryUiModel
import com.optlab.banhangso.features.main.category.models.mappers.CategoryUiModelMapper
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel
import com.optlab.banhangso.models.application.AppError
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.repositories.interfaces.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.processors.BehaviorProcessor
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel
    @Inject
    constructor(private val categoryRepository: CategoryRepository) : RxViewModel() {
        private val _searchQuery: ObservableField<String> = ObservableField("")
        val searchQuery: ObservableField<String>
            get() = _searchQuery

        private val searchProcessor: BehaviorProcessor<String> = BehaviorProcessor.createDefault("")

        private val _deletionCompleted = MutableLiveData<Unit>()
        val deletionCompleted: LiveData<Unit> = _deletionCompleted

        private val _categories: Flowable<PagingData<CategoryUiModel>> by lazy {
            searchProcessor
                .distinctUntilChanged()
                .doOnNext { Timber.d("Category search query {$it}") }
                .switchMap { query ->
                    if (query.isBlank()) {
                        categoryRepository.categories
                    } else {
                        categoryRepository.searchCategories(query)
                    }
                        .map { pagingData -> pagingData.map(CategoryUiModelMapper::fromDomain) }
                }
        }
        val categories: Flowable<PagingData<CategoryUiModel>>
            get() = _categories

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

        fun deleteCategory(categoryId: String) {
            val disposable =
                categoryRepository
                    .deleteCategory(categoryId)
                    .subscribeOn(Schedulers.io())
                    .doOnSuccess { _ -> isLoading.postValue(true) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally {
                        isLoading.value = false
                        _deletionCompleted.value = Unit
                    }
                    .subscribe(this::onDeleteCategorySuccess, this::onDeleteCategoryError)

            disposables.add(disposable)
        }

        private fun onDeleteCategoryError(throwable: Throwable) {
            messageResId.value = R.string.error_unknown
            Timber.e(throwable, "There was an error while deleting category: %s", throwable.message)
        }

        private fun onDeleteCategorySuccess(result: Result<Void>) {
            when (result) {
                is Result.Failure<Void> -> {
                    when (result.error) {
                        is AppError.ForbiddenError -> R.string.error_forbidden
                        is AppError.NetServiceError -> R.string.error_network
                        is AppError.NotFoundError -> R.string.error_category_not_found
                        else -> R.string.error_unknown
                    }
                }
                is Result.Success<Void> -> {
                    R.string.notify_category_delete_success
                }
            }.also { messageResId.value = it }
        }
    }
