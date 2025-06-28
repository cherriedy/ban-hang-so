package com.optlab.banhangso.features.main.store.viewmodel;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class StoreEditViewModel extends ViewModel {
    private final StoreRepository storeRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Store> store = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> saveResult = new MutableLiveData<>(false);

    @Inject
    public StoreEditViewModel(StoreRepository storeRepository, AuthRepository authRepository) {
        this.storeRepository = storeRepository;
        this.authRepository = authRepository;
    }

    @Override
    protected void onCleared() {
        disposables.clear();
        super.onCleared();
    }

    public LiveData<Store> getStore() {
        return store;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getSaveResult() {
        return saveResult;
    }

    @SuppressLint("CheckResult")
    public void loadStoreById(@NonNull String storeId) {
        Store currentStore = store.getValue();
        if (currentStore == null) {
            if (storeId.isEmpty()) {
                store.setValue(new Store());
            } else {
                disposables.add(
                        storeRepository
                                .getStore(storeId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnSubscribe(unused -> onGetStoreSubscribe(storeId))
                                .subscribe(this::onGetStoreSuccess, this::onGetStoreFailure));
            }
        }
    }

    private void onGetStoreSubscribe(@NonNull String storeId) {
        isLoading.setValue(true);
        Timber.d("Starting to fetch store with ID: %s", storeId);
    }

    private void onGetStoreFailure(Throwable throwable) {
        Timber.e(throwable, "Error fetching store: %s", throwable.getMessage());
    }

    private void onGetStoreSuccess(Result<Store> result) {
        // TODO: Handle the result of fetching the store
        //        isProcessing.setValue(false);
        //        if (result.status == Result.Status.SUCCESS && result.data != null) {
        //            Timber.d("Loaded store successfully: %s", result.data);
        //            store.setValue(result.data);
        //        } else {
        //            Timber.e("Error fetching store: %s", result.message);
        //            store.setValue(null);
        //        }
    }

    /**
     * @noinspection unused
     */
    public void onSaveButtonClick(View view) {
        Store pendingStore = this.store.getValue();
        if (pendingStore != null) {

            Disposable disposable =
                    authRepository
                            .getUser()
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(__ -> isLoading.postValue(true))
                            .flatMap(
                                    result -> {
                                        if (result instanceof Result.Success<User> success
                                                && success.getData() != null) {
                                            return Single.just(success.getData());
                                        }
                                        return Single.error(
                                                new Exception("No User instance found"));
                                    })
                            .flatMap(user -> storeRepository.setStore(user.getId(), pendingStore))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> isLoading.setValue(false))
                            .subscribe(this::onSaveStoreSuccess, this::onSaveStoreError);

            disposables.add(disposable);
        }
    }

    private void onSaveStoreError(Throwable throwable) {
        saveResult.setValue(false);
        Timber.e(throwable, "Error saving store: %s", throwable.getMessage());
    }

    private void onSaveStoreSuccess(Result<String> result) {
        if (result instanceof Result.Success<String> success) {
            saveResult.setValue(success.getData() != null);
        } else {
            saveResult.setValue(false);
        }
    }
}
