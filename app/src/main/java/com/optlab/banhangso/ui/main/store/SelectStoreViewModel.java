package com.optlab.banhangso.ui.main.store;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.domain.model.Store;
import com.optlab.banhangso.domain.repository.StoreRepository;
import com.optlab.banhangso.domain.util.Resource;

import dagger.hilt.android.lifecycle.HiltViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import timber.log.Timber;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

@HiltViewModel
public class SelectStoreViewModel extends ViewModel {
    private final StoreRepository storeRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<List<Store>> stores = new MutableLiveData<>(new ArrayList<>());

    @Inject
    public SelectStoreViewModel(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
        observeStores();
    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }

    public LiveData<List<Store>> getStores() {
        return stores;
    }

    private void observeStores() {
        disposable.add(
                storeRepository
                        .getAllStores()
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(unused -> Timber.d("Starting to fetch stores..."))
                        .subscribe(
                                this::handleStoreResource,
                                e -> Timber.e(e, "Error fetching stores: %s", e.getMessage())));
    }

    private void handleStoreResource(Resource<List<Store>> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            stores.setValue(resource.data);
            Timber.d("Loaded stores successfully: %s", resource.data.size());
        } else if (resource.status == Resource.Status.LOADING && resource.data != null) {
            Timber.d("Loading stores: %s", resource.data.size());
        } else if (resource.status == Resource.Status.ERROR) {
            Timber.e("Failed to load stores: %s", resource.message);
        }
    }
}
