package com.optlab.banhangso.features.main.store.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.map
import androidx.paging.rxjava3.cachedIn
import com.optlab.banhangso.R
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel
import com.optlab.banhangso.features.main.store.models.mappers.RoleStoreUiModelMapper
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel
import com.optlab.banhangso.models.application.AppError
import com.optlab.banhangso.models.application.Result
import com.optlab.banhangso.models.domain.User
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt
import com.optlab.banhangso.repositories.interfaces.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SelectStoreViewModel
    @Inject
    constructor(
        private val storeRepository: StoreRepository,
        private val preferencesRepositoryKt: PreferencesRepositoryKt,
    ) : RxViewModel() {
        @OptIn(ExperimentalCoroutinesApi::class)
        private val _stores: Flowable<PagingData<RoleStoreUiModel>> =
            storeRepository.userStores
                .map { pagingData -> pagingData.map(RoleStoreUiModelMapper::fromDomain) }
                .cachedIn(viewModelScope)

        val stores: Flowable<PagingData<RoleStoreUiModel>>
            get() = _stores

        private val _selectStoreResult: MutableLiveData<Boolean> = MutableLiveData()
        val selectStoreResult: MutableLiveData<Boolean>
            get() = _selectStoreResult

        private val _refresh: MutableLiveData<Boolean> = MutableLiveData()
        val refresh: MutableLiveData<Boolean>
            get() = _refresh

        private val _navigateToAuthentication: MutableLiveData<Boolean> = MutableLiveData()
        val navigateToAuthentication: MutableLiveData<Boolean>
            get() = _navigateToAuthentication

        private val _user: MutableLiveData<User> = MutableLiveData()
        val user: MutableLiveData<User>
            get() = _user

        init {
            preferencesRepositoryKt
                .getUserRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onGetUserSuccess, this::onGetUserError)
                .also { disposables.add(it) }
        }

        private fun onGetUserSuccess(user: User) {
            _user.value = user
            Timber.d("The user has been retrieved successfully: $user")
        }

        private fun onGetUserError(throwable: Throwable) {
            messageResId.value = R.string.error_unknown
            Timber.e(throwable, "There was an error retrieving the user: ${throwable.message}")
        }

        fun setSelectedStore(roleStoreUiModel: RoleStoreUiModel) =
            RoleStoreUiModelMapper.toDomain(roleStoreUiModel).let { roleStore ->
                preferencesRepositoryKt
                    .setStoreRx(roleStore)
                    .flatMap { success ->
                        if (success) {
                            preferencesRepositoryKt.setStoreSelectedRx(true)
                        } else {
                            Single.just(false)
                        }
                    }
                    .subscribeOn(Schedulers.io())
                    .doOnSubscribe { isLoading.postValue(true) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doFinally { isLoading.value = false }
                    .subscribe(this::onSetSelectedStoreSuccess, this::onSetSelectedStoreError)
                    .also { disposables.add(it) }
            }

        private fun onSetSelectedStoreSuccess(result: Boolean) {
            _selectStoreResult.value = result
            Timber.d("The store has been set successfully: $result")
        }

        private fun onSetSelectedStoreError(throwable: Throwable) {
            _selectStoreResult.value = false
            messageResId.value = R.string.error_unknown
            Timber.e(throwable, "There was an error setting the store: ${throwable.message}")
        }

        fun deleteStore(storeId: String) {
            storeRepository
                .deleteStore(storeId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { isLoading.postValue(true) }
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally {
                    isLoading.value = false
                    _refresh.value = true
                }
                .subscribe(this::onDeleteStoreSuccess, this::onDeleteStoreError)
                .let { disposables.add(it) }
        }

        private fun onDeleteStoreSuccess(result: Result<Void>) {
            fun clearPreferencesStore() {
                preferencesRepositoryKt
                    .clearStore()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        {
                            messageResId.value = R.string.notify_delete_store_success
                        },
                        { throwable ->
                            messageResId.value = R.string.error_unknown
                            Timber.e(
                                throwable,
                                "There was an error clearing the store: ${throwable.message}",
                            )
                        },
                    )
                    .also { disposables.add(it) }
            }

            when (result) {
                is Result.Success -> {
                    clearPreferencesStore()
                }
                is Result.Failure ->
                    when (result.error) {
                        is AppError.NetServiceError -> messageResId.value = R.string.error_network
                        is AppError.ForbiddenError -> messageResId.value = R.string.error_forbidden
                        else -> messageResId.value = R.string.error_unknown
                    }
            }
        }

        private fun onDeleteStoreError(throwable: Throwable) {
            _refresh.value = false
            messageResId.value = R.string.error_unknown
            Timber.e(throwable, "There was an error deleting the store: ${throwable.message}")
        }

        fun onSignOut() {
            preferencesRepositoryKt
                .clearStore()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        _navigateToAuthentication.value = true
                    },
                    { throwable ->
                        messageResId.value = R.string.error_unknown
                        Timber.e(throwable, "There was an error clearing the store: ${throwable.message}")
                    },
                )
                .also { disposables.add(it) }
        }
    }
