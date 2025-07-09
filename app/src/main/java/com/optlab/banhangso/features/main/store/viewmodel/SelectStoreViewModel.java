package com.optlab.banhangso.features.main.store.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel;
import com.optlab.banhangso.features.main.store.models.mappers.RoleStoreUiModelMapper;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.List;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class SelectStoreViewModel extends ViewModel {

  private final AuthRepository authRepository;
  private final StoreRepository storeRepository;
  private final PreferencesRepository preferencesRepository;
  private final CompositeDisposable disposables = new CompositeDisposable();
  private final MutableLiveData<User> user = new MutableLiveData<>();
  private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
  private final MutableLiveData<Boolean> setStoreResult = new MutableLiveData<>();
  private final MutableLiveData<List<RoleStoreUiModel>> stores = new MutableLiveData<>();

  @Inject
  public SelectStoreViewModel(
      AuthRepository authRepository,
      StoreRepository storeRepository,
      PreferencesRepository preferencesRepository) {
    this.authRepository = authRepository;
    this.storeRepository = storeRepository;
    this.preferencesRepository = preferencesRepository;

    observeUserUpdates();
  }

  private void observeUserUpdates() {
    Disposable disposable =
        authRepository
            .getUser()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handleUserResult);

    disposables.add(disposable);
  }

  private void handleUserResult(Result<User> userResult, Throwable throwable) {
    if (throwable != null) {
      Timber.e(throwable, "Error fetching user: %s", throwable.getMessage());
      return;
    }

    if (userResult instanceof Result.Success<User> success) {
      user.setValue(success.getData()); // Update user LiveData
      retrieveStores(); // Fetch stores after user is fetched
      Timber.d("User fetched successfully: %s", userResult);
    } else if (userResult instanceof Result.Failure<User> failure) {
      // TODO: handle failure case, e.g., show error message
    }
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    super.onCleared();
  }

  public LiveData<List<RoleStoreUiModel>> getStores() {
    return stores;
  }

  public LiveData<Boolean> getIsLoading() {
    return isLoading;
  }

  public LiveData<User> getUser() {
    return user;
  }

  public LiveData<Boolean> getSetStoreResult() {
    return setStoreResult;
  }

  public void retrieveStores() {
    User authenticatedUser = user.getValue();
    if (authenticatedUser == null) {
      Timber.w("User is not authenticated, cannot retrieve stores");
      return;
    }

    Disposable disposable =
        storeRepository
            .getUserStores(authenticatedUser.getId())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe(__ -> isLoading.setValue(true))
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onGetUserStoresSuccess, this::onGetUserStoresFailure);

    disposables.add(disposable);
  }

  private void onGetUserStoresFailure(Throwable e) {
    Timber.e(e, "Error fetching stores: %s", e.getMessage());
  }

  private void onGetUserStoresSuccess(Result<List<RoleStore>> result) {
    if (result instanceof Result.Success<List<RoleStore>> success) {
      List<RoleStore> roleStores = success.getData();

      if (roleStores == null || roleStores.isEmpty()) {
        Timber.w("No stores found for the user");
        stores.setValue(null);
        return;
      }

      Timber.d("Fetched %d stores successfully", roleStores.size());
      stores.setValue(RoleStoreUiModelMapper.fromDomains(roleStores));
    } else if (result instanceof Result.Failure failure) {
      // TODO: handle failure case, e.g., show error message
    }
  }

  public void setSelectedStore(@NonNull RoleStoreUiModel roleStoreUiModel) {
    RoleStore store = RoleStoreUiModelMapper.toDomain(roleStoreUiModel);
    Disposable disposable =
        preferencesRepository
            .setStore(store)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onSetSelectedStoreSuccess, this::onSetSelectedStoreError);

    disposables.add(disposable);
  }

  private void onSetSelectedStoreSuccess() {
    Timber.d("Selected store set successfully");
    setStoreResult.setValue(true);
  }

  private void onSetSelectedStoreError(Throwable throwable) {
    Timber.e(throwable, "Error setting selected store: %s", throwable.getMessage());
    setStoreResult.setValue(false);
  }
}
