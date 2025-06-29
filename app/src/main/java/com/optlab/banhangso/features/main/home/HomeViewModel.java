package com.optlab.banhangso.features.main.home;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@HiltViewModel
public class HomeViewModel extends ViewModel {

  private final AuthRepository authRepository;
  private final PreferencesRepository preferencesRepository;
  private final CompositeDisposable disposables = new CompositeDisposable();
  private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
  private final MutableLiveData<Boolean> signOutResult = new MutableLiveData<>();

  private LiveData<User> user;
  private LiveData<RoleStore> store;

  @Inject
  public HomeViewModel(
      AuthRepository authRepository, @NonNull PreferencesRepository preferencesRepository) {
    this.authRepository = authRepository;
    this.preferencesRepository = preferencesRepository;

    user = LiveDataReactiveStreams.fromPublisher(preferencesRepository.getUser().toFlowable());
    store = LiveDataReactiveStreams.fromPublisher(preferencesRepository.getStore().toFlowable());
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    super.onCleared();
  }

  public LiveData<Boolean> isLoading() {
    return isLoading;
  }

  public LiveData<Boolean> getSignOutResult() {
    return signOutResult;
  }

  public LiveData<RoleStore> getStore() {
    return store;
  }

  public LiveData<User> getUser() {
    return user;
  }

  public void onSignOut(@NonNull View view) {
    Disposable disposable =
        authRepository
            .signOut()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onSignOutSuccess, this::onbSignOutError);

    disposables.add(disposable);
  }

  private void onSignOutSuccess() {
    signOutResult.setValue(true);
  }

  private void onbSignOutError(Throwable throwable) {
    signOutResult.setValue(false);
    Timber.e(throwable, "There was an error while signing out: %s", throwable.getMessage());
  }
}
