package com.optlab.banhangso.features.main.authentication.viewmodel;

import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_STORE_CODE;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_STORE_DESCRIPTION;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_STORE_NAME;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_TERMS_AND_CONDITIONS;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_USER_NAME;
import static com.optlab.banhangso.features.main.authentication.Constants.ERROR_USER_PHONE;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_STORE_CODE;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_STORE_DESCRIPTION;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_STORE_NAME;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_USER_NAME;
import static com.optlab.banhangso.features.main.authentication.Constants.KEY_USER_PHONE;
import static com.optlab.banhangso.internal.Config.OWNER;
import static com.optlab.banhangso.internal.Config.STAFF;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.authentication.AuthValidator;
import com.optlab.banhangso.features.main.authentication.Constants;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.remote.requestes.SignUpRequest;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed, LombokSetterMayBeUsed
 */
@HiltViewModel
public class RegistrationViewModel extends ViewModel {

  private final SavedStateHandle savedStateHandle;
  private final AuthValidator validator;
  private final AuthRepository authRepository;
  private final CompositeDisposable disposables = new CompositeDisposable();
  private final ObservableArrayMap<String, String> inputFields = new ObservableArrayMap<>();
  private final ObservableArrayMap<String, String> errors = new ObservableArrayMap<>();
  private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
  private final MutableLiveData<Boolean> signUpResult = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canRegister = new MutableLiveData<>();
  private final MutableLiveData<Integer> errorMessageResId = new MutableLiveData<>();

  private String email;
  private String password;

  private final ObservableMap.OnMapChangedCallback<ObservableMap<String, String>, String, String>
      inputFieldsCallback =
          new ObservableMap.OnMapChangedCallback<>() {
            @Override
            public void onMapChanged(ObservableMap<String, String> sender, @NonNull String key) {
              if (key == null) {
                return;
              }

              switch (key) {
                case KEY_USER_NAME -> validateContactName();
                case KEY_USER_PHONE -> validateContactPhone();
                case KEY_STORE_NAME -> validateStoreName();
                case KEY_STORE_CODE -> validateStoreCode();
                case KEY_STORE_DESCRIPTION -> validateStoreDescription();
              }
            }
          };

  @Inject
  public RegistrationViewModel(
      @NonNull SavedStateHandle savedStateHandle,
      AuthValidator validator,
      AuthRepository authRepository) {
    this.savedStateHandle = savedStateHandle;
    this.validator = validator;
    this.authRepository = authRepository;

    Boolean isAdmin = savedStateHandle.get(Constants.KEY_IS_OWNER);
    savedStateHandle.set(Constants.KEY_IS_OWNER, isAdmin == null || isAdmin);

    initInputFields();
  }

  private void initInputFields() {
    // Initialize ObservableMap with empty values
    inputFields.put(KEY_USER_NAME, "");
    inputFields.put(KEY_USER_PHONE, "");
    inputFields.put(KEY_STORE_NAME, "");
    inputFields.put(KEY_STORE_DESCRIPTION, "");
    inputFields.put(KEY_STORE_CODE, "");

    // Initialize map callback
    inputFields.addOnMapChangedCallback(inputFieldsCallback);
  }

  @Override
  protected void onCleared() {
    inputFields.clear();
    errors.clear();
    super.onCleared();
  }

  /**
   * Returns the observable map containing user and store input fields. This map is used for two-way
   * data binding in the UI.
   *
   * @return The ObservableMap instance containing input data
   */
  public ObservableArrayMap<String, String> getInputFields() {
    return inputFields;
  }

  public ObservableArrayMap<String, String> getErrors() {
    return errors;
  }

  public LiveData<Boolean> getIsAdmin() {
    return savedStateHandle.getLiveData(Constants.KEY_IS_OWNER);
  }

  public LiveData<Integer> getErrorMessageResId() {
    return errorMessageResId;
  }

  public void setIsAdmin(boolean isAdmin) {
    savedStateHandle.set(Constants.KEY_IS_OWNER, isAdmin);
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LiveData<Boolean> getIsLoading() {
    return isLoading;
  }

  public LiveData<Boolean> getSignUpResult() {
    return signUpResult;
  }

  public LiveData<Boolean> canRegister() {
    return canRegister;
  }

  private void setErrors(@NonNull Consumer<ObservableMap<String, String>> mapConsumer) {
    ObservableMap<String, String> map = errors;
    mapConsumer.accept(map);
    errors.putAll(map);
    updateCanRegister();
  }

  private void updateCanRegister() {
    canRegister.setValue(errors.isEmpty());
  }

  public void validateContactName() {
    setErrors(
        errorMap -> {
          String name = inputFields.get(KEY_USER_NAME);
          if (name != null) {
            String error = validator.validateContactName(name);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_USER_NAME, error);
            } else {
              errorMap.remove(ERROR_USER_NAME);
            }
          }
        });
  }

  public void validateContactPhone() {
    setErrors(
        errorMap -> {
          String phone = inputFields.get(KEY_USER_PHONE);
          if (phone != null) {
            String error = validator.validatePhoneNumber(phone);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_USER_PHONE, error);
            } else {
              errorMap.remove(ERROR_USER_PHONE);
            }
          }
        });
  }

  public void validateStoreName() {
    setErrors(
        errorMap -> {
          String name = inputFields.get(KEY_STORE_NAME);
          if (name != null) {
            String error = validator.validateStoreName(name);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_STORE_NAME, error);
            } else {
              errorMap.remove(ERROR_STORE_NAME);
            }
          }
        });
  }

  public void validateStoreDescription() {
    setErrors(
        errorMap -> {
          String description = inputFields.get(KEY_STORE_DESCRIPTION);
          if (description != null) {
            String error = validator.validateStoreDescription(description);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_STORE_DESCRIPTION, error);
            } else {
              errorMap.remove(ERROR_STORE_DESCRIPTION);
            }
          }
        });
  }

  public void validateStoreCode() {
    setErrors(
        errorMap -> {
          String code = inputFields.get(KEY_STORE_CODE);
          if (code != null) {
            String error = validator.validateStoreCode(code);
            if (!error.isEmpty()) {
              errorMap.put(ERROR_STORE_CODE, error);
            } else {
              errorMap.remove(ERROR_STORE_CODE);
            }
          }
        });
  }

  public void validateTermsAndConditions(boolean isChecked) {
    setErrors(
        errorMap -> {
          String error = validator.validateAgreeTermsAndConditions(isChecked);
          if (!error.isEmpty()) {
            errorMap.put(ERROR_TERMS_AND_CONDITIONS, error);
          } else {
            errorMap.remove(ERROR_TERMS_AND_CONDITIONS);
          }
        });
  }

  public void onSignUp(@NonNull View view) {
    SignUpRequest signUpRequest = getSignUpRequestObject();

    Disposable disposable =
        authRepository
            .signUpWithEmailAndPassword(signUpRequest)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(
                __ -> {
                  isLoading.postValue(true);
                  Timber.d("Starting sign-up process...");
                })
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(
                () -> {
                  isLoading.setValue(false);
                  Timber.d("Sign-up process completed.");
                })
            .subscribe(this::onSignUpSuccess, this::onSignUpError);

    disposables.add(disposable);
  }

  private SignUpRequest getSignUpRequestObject() {
    String displayName = inputFields.get(KEY_USER_NAME);
    String phone = inputFields.get(KEY_USER_PHONE);
    String storeId = inputFields.get(KEY_STORE_CODE);
    String storeName = inputFields.get(KEY_STORE_NAME);
    String storeDescription = inputFields.get(KEY_STORE_DESCRIPTION);
    String role = (storeId != null && !storeId.isBlank()) ? STAFF : OWNER;

    SignUpRequest.StoreInfo storeInfo = null;
    if (OWNER.equals(role)) {
      storeInfo =
          SignUpRequest.StoreInfo.builder().name(storeName).description(storeDescription).build();
    }

    return SignUpRequest.builder()
        .email(email)
        .password(password)
        .role(role)
        .contactName(displayName)
        .phone(phone)
        .storeInfo(storeInfo)
        .storeId(storeId)
        .build();
  }

  private void onSignUpSuccess(@NonNull Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      signUpResult.setValue(true);
    } else if (result instanceof Result.Failure<Void> failure) {
      signUpResult.setValue(false);
      if (failure.getError() instanceof AppError.InvalidArgument) {
        errorMessageResId.setValue(R.string.error_invalid_inputs);
      } else if (failure.getError() instanceof AppError.DuplicateError) {
        errorMessageResId.setValue(R.string.error_email_already_in_use);
      } else {
        errorMessageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onSignUpError(Throwable throwable) {
    signUpResult.setValue(false);
    errorMessageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error during sign-up: %s", throwable.getMessage());
  }
}
