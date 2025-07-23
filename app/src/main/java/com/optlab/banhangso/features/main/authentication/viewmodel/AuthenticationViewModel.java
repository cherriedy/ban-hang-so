package com.optlab.banhangso.features.main.authentication.viewmodel;

import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_CONFIRM_PASSWORD;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_EMAIL;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_PASSWORD;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_CONFIRM_PASSWORD;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_EMAIL;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_IS_SIGN_IN;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_PASSWORD;

import android.util.Pair;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import com.optlab.banhangso.features.main.authentication.AuthValidator;
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.function.Consumer;
import javax.inject.Inject;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@HiltViewModel
public class AuthenticationViewModel extends RxViewModel {

  private final SavedStateHandle savedStateHandle;
  private final AuthValidator validator;
  private final AuthRepository authRepository;
  private final PreferencesRepositoryKt preferencesRepositoryKt;
  private final MutableLiveData<Boolean> registrationFlag = new MutableLiveData<>(false);
  private final MutableLiveData<Boolean> canAuthenticate = new MutableLiveData<>(false);
  private final ObservableArrayMap<String, String> inputFields = new ObservableArrayMap<>();
  private final ObservableArrayMap<String, String> errors = new ObservableArrayMap<>();
  private final MutableLiveData<Boolean> errorFlag = new MutableLiveData<>();
  private final MutableLiveData<Boolean> isChecking = new MutableLiveData<>(false);

  /**
   * The first value indicates whether the user is authenticated, and the second value indicates
   * whether the store is empty or not.
   */
  private final LiveData<Pair<Boolean, Boolean>> authState;

  /** LiveData that clears errors when sign-in state changes */
  private final LiveData<Boolean> errorClearingSignInState;

  private final LiveData<Boolean> isAuthenticatedLiveData;
  private final LiveData<RoleStore> storeLiveData;

  @Inject
  public AuthenticationViewModel(
      SavedStateHandle savedStateHandle,
      @NonNull PreferencesRepositoryKt preferencesRepositoryKt,
      AuthValidator validator,
      @NonNull AuthRepository authRepository) {
    this.savedStateHandle = savedStateHandle;
    this.preferencesRepositoryKt = preferencesRepositoryKt;
    this.validator = validator;
    this.authRepository = authRepository;

    isAuthenticatedLiveData =
        LiveDataReactiveStreams.fromPublisher(
            preferencesRepositoryKt
                .isAuthenticatedRx()
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(__ -> isChecking.postValue(true))
                .observeOn(AndroidSchedulers.mainThread()));

    storeLiveData =
        LiveDataReactiveStreams.fromPublisher(
            preferencesRepositoryKt
                .getStoreRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));

    errorClearingSignInState = initSignInState();
    authState = Transformations.switchMap(isAuthenticatedLiveData, this::handleAuthenticationState);

    initAuthInputsListener();
  }

  @NonNull private LiveData<Pair<Boolean, Boolean>> handleAuthenticationState(
      @NonNull Boolean isAuthenticated) {
    if (!isAuthenticated) {
      return new MutableLiveData<>(new Pair<>(false, null));
    } else {
      return Transformations.map(
          storeLiveData, store -> new Pair<>(true, store != null && !store.isEmpty()));
    }
  }

  public ObservableArrayMap<String, String> getInputFields() {
    return this.inputFields;
  }

  public ObservableArrayMap<String, String> getErrors() {
    return this.errors;
  }

  private void initAuthInputsListener() {
    ObservableMap.OnMapChangedCallback<ObservableMap<String, String>, String, String>
        authInputsCallback =
            new ObservableMap.OnMapChangedCallback<>() {
              @Override
              public void onMapChanged(ObservableMap<String, String> sender, String key) {
                if (key == null) {
                  return;
                }

                switch (key) {
                  case KEY_EMAIL -> validateEmail();
                  case KEY_PASSWORD -> validatePassword();
                  case KEY_CONFIRM_PASSWORD -> {
                    if (Boolean.FALSE.equals(isSignIn().getValue())) {
                      validateConfirmPassword();
                    }
                  }
                  default -> Timber.w("Unhandled key change: %s", key);
                }
              }
            };
    inputFields.addOnMapChangedCallback(authInputsCallback);
  }

  @Override
  protected void onCleared() {
    inputFields.clear();
    errors.clear();
    super.onCleared();
  }

  @NonNull private LiveData<Boolean> initSignInState() {
    // Retrieve the sign-in state from SavedStateHandle
    Boolean isSignIn = savedStateHandle.get(KEY_IS_SIGN_IN);
    // If the sign-in state is not set, default to true (sign-in mode)
    savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn == null || isSignIn);

    return Transformations.map(
        savedStateHandle.getLiveData(KEY_IS_SIGN_IN, false),
        signedIn -> {
          errors.clear();
          return signedIn;
        });
  }

  public LiveData<Boolean> isSignIn() {
    return savedStateHandle.getLiveData(KEY_IS_SIGN_IN);
  }

  public void setIsSignIn(boolean isSignIn) {
    savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn);
  }

  public LiveData<Boolean> isChecking() {
    return isChecking;
  }

  public LiveData<Boolean> getRegistrationFlag() {
    return registrationFlag;
  }

  public LiveData<Boolean> canAuthenticate() {
    return canAuthenticate;
  }

  public void setRegistrationFlag(boolean shouldNavigate) {
    registrationFlag.setValue(shouldNavigate);
  }

  public LiveData<Pair<Boolean, Boolean>> getAuthState() {
    return authState;
  }

  public LiveData<Boolean> getErrorFlag() {
    return errorFlag;
  }

  private void setErrors(@NonNull Consumer<ObservableMap<String, String>> mapConsumer) {
    ObservableMap<String, String> map = errors;
    mapConsumer.accept(map);
    errors.putAll(map);
    updateCanAuthenticate();
  }

  private void updateCanAuthenticate() {
    canAuthenticate.setValue(errors.isEmpty());
  }

  private void validateEmail() {
    setErrors(
        errorMap -> {
          String email = inputFields.get(KEY_EMAIL);
          if (email != null) {
            String error = validator.validateEmail(email);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_EMAIL, error);
            } else {
              errorMap.remove(ERROR_EMAIL);
            }
          }
        });
  }

  private void validatePassword() {
    setErrors(
        errorMap -> {
          String password = inputFields.get(KEY_PASSWORD);
          if (password != null) {
            String error = validator.validatePassword(password, false);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_PASSWORD, error);
            } else {
              errorMap.remove(ERROR_PASSWORD);
            }
          }
        });
  }

  private void validateConfirmPassword() {
    setErrors(
        errorMap -> {
          String password = inputFields.get(KEY_PASSWORD);
          String confirmPassword = inputFields.get(KEY_CONFIRM_PASSWORD);
          if (password != null && confirmPassword != null) {
            String error = validator.validateConfirmPassword(password, confirmPassword);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_CONFIRM_PASSWORD, error);
            } else {
              errorMap.remove(ERROR_CONFIRM_PASSWORD);
            }
          }
        });
  }

  /**
   * @noinspection unused
   */
  public void onAuthenticate(View view) {
    String email = inputFields.get(KEY_EMAIL);
    String password = inputFields.get(KEY_PASSWORD);

    if (email == null || password == null) {
      Timber.e("Authentication data is null");
      return;
    }

    if (Boolean.TRUE.equals(isSignIn().getValue())) {

      Disposable disposable =
          authRepository
              .logInWithEmailAndPassword(email, password)
              .subscribeOn(Schedulers.io())
              .doOnSubscribe(__ -> isLoading.postValue(true))
              .observeOn(AndroidSchedulers.mainThread())
              .doFinally(() -> isLoading.setValue(false))
              .subscribe(this::onLogInSuccess, this::onLogInError);

      disposables.add(disposable);
    } else {
      registrationFlag.setValue(true);
    }
  }

  private void onLogInError(Throwable throwable) {
    errorFlag.setValue(true);
    Timber.e(throwable, "Authentication failed: %s", throwable.getMessage());
  }

  private void onLogInSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      Timber.d("Authentication successful");
    } else if (result instanceof Result.Failure<Void> failure) {
      if (failure.getError() instanceof AppError.UnknownError) {
        errorFlag.setValue(true);
      }
    }
  }
}
