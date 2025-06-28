package com.optlab.banhangso.features.main.authentication.viewmodel;

import static com.optlab.banhangso.internal.utilities.Constants.Auth.ERROR_CONFIRM_PASSWORD;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.ERROR_EMAIL;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.ERROR_PASSWORD;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_CONFIRM_PASSWORD;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_EMAIL;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_IS_SIGN_IN;
import static com.optlab.banhangso.internal.utilities.Constants.Auth.KEY_PASSWORD;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.internal.validators.AuthValidator;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import lombok.NonNull;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@HiltViewModel
public class AuthenticationViewModel extends ViewModel {

    private final SavedStateHandle savedStateHandle;
    private final AuthValidator validator;
    private final AuthRepository authRepository;
    private final UserRepository userRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> authResult = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> registrationFlag = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> canAuthenticate = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoggedIn = new MutableLiveData<>(false);
    private final ObservableArrayMap<String, String> inputs = new ObservableArrayMap<>();
    private final ObservableArrayMap<String, String> errors = new ObservableArrayMap<>();

    private Observer<Object> signInFlagObserver;

    @Inject
    public AuthenticationViewModel(
            SavedStateHandle savedStateHandle,
            AuthValidator validator,
            AuthRepository authRepository,
            UserRepository userRepository) {
        this.savedStateHandle = savedStateHandle;
        this.validator = validator;
        this.authRepository = authRepository;
        this.userRepository = userRepository;

        checkLoginStatus();
        initAuthInputsListener();
        initSignInState();
    }

    public ObservableArrayMap<String, String> getInputs() {
        return this.inputs;
    }

    public ObservableArrayMap<String, String> getErrors() {
        return this.errors;
    }

    private void checkLoginStatus() {
        Disposable disposable =
                authRepository
                        .isLoggedIn()
                        .subscribe(
                                (Result<Boolean> result) -> {
                                    if (result instanceof Result.Success<Boolean> success) {
                                        isLoggedIn.setValue(success.getData());
                                    }
                                });

        disposables.add(disposable);
    }

    private void initAuthInputsListener() {
        ObservableMap.OnMapChangedCallback<ObservableMap<String, String>, String, String>
                authInputsCallback =
                new ObservableMap.OnMapChangedCallback<>() {
                    @Override
                    public void onMapChanged(
                            ObservableMap<String, String> sender, String key) {
                        if (key == null) {
                            return; // Skip processing if key is null
                        }

                        switch (key) {
                            case KEY_EMAIL -> validateEmail();
                            case KEY_PASSWORD -> validatePassword();
                            case KEY_CONFIRM_PASSWORD -> {
                                if (Boolean.FALSE.equals(isSignIn().getValue())) {
                                    validateConfirmPassword();
                                }
                            }
                            default -> throw new IllegalStateException(
                                    "Unexpected value: " + key);
                        }
                    }
                };
        inputs.addOnMapChangedCallback(authInputsCallback);
    }

    @Override
    protected void onCleared() {
        inputs.clear();
        errors.clear();

        savedStateHandle.getLiveData(KEY_IS_SIGN_IN).removeObserver(signInFlagObserver);
        savedStateHandle.remove(KEY_IS_SIGN_IN);

        disposables.clear();
        super.onCleared();
    }

    private void initSignInState() {
        // Observer to clear errors when the sign-in flag changes
        signInFlagObserver = isSignIn -> errors.clear();
        // Retrieve the sign-in state from SavedStateHandle
        Boolean isSignIn = savedStateHandle.get(KEY_IS_SIGN_IN);
        // If the sign-in state is not set, default to true (sign-in mode)
        savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn == null || isSignIn);
        // Observe changes to the sign-in flag to clear errors
        savedStateHandle.getLiveData(KEY_IS_SIGN_IN).observeForever(signInFlagObserver);
    }

    public LiveData<Boolean> isSignIn() {
        return savedStateHandle.getLiveData(KEY_IS_SIGN_IN);
    }

    public void setIsSignIn(boolean isSignIn) {
        savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn);
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getAuthResult() {
        return authResult;
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

    public LiveData<Boolean> isLoggedIn() {
        return isLoggedIn;
    }

    private void setErrors(@NonNull Consumer<ObservableMap<String, String>> mapConsumer) {
        ObservableMap<String, String> map = errors;
        mapConsumer.accept(map);
        errors.putAll(map);
        updateCanAuthenticate();
    }

    private void updateCanAuthenticate() {
//        boolean emailError =
//                errors.containsKey(ERROR_EMAIL)
//                        && Objects.requireNonNull(errors.get(ERROR_EMAIL)).isBlank();
//        boolean passwordError =
//                errors.containsKey(ERROR_PASSWORD)
//                        && Objects.requireNonNull(errors.get(ERROR_PASSWORD)).isBlank();
//        boolean confirmPasswordError =
//                errors.containsKey(ERROR_CONFIRM_PASSWORD)
//                        && Objects.requireNonNull(errors.get(ERROR_CONFIRM_PASSWORD)).isBlank();
//
//        if (Boolean.TRUE.equals(savedStateHandle.get(KEY_IS_SIGN_IN))) {
//            canAuthenticate.setValue(emailError && passwordError);
//        } else {
//            canAuthenticate.setValue(emailError && passwordError && confirmPasswordError);
//        }

        canAuthenticate.setValue(errors.isEmpty());
    }

    private void validateEmail() {
        setErrors(
                errorMap -> {
                    String email = inputs.get(KEY_EMAIL);
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
                    String password = inputs.get(KEY_PASSWORD);
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
                    String password = inputs.get(KEY_PASSWORD);
                    String confirmPassword = inputs.get(KEY_CONFIRM_PASSWORD);
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
    @SuppressLint("CheckResult")
    public void onAuthenticate(View view) {
        String email = inputs.get(KEY_EMAIL);
        String password = inputs.get(KEY_PASSWORD);

        if (email == null || password == null) {
            Timber.e("Authentication data is null");
            return;
        }

        if (Boolean.TRUE.equals(isSignIn().getValue())) {
            isLoading.setValue(true);

            Disposable disposable =
                    authRepository
                            .logInWithEmailAndPassword(email, password)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(
                                    __ -> Timber.d("Starting authentication for email: %s", email))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    result -> {
                                        setAuthenticationResult(
                                                result instanceof Result.Success<Void>);
                                        Timber.d(
                                                "Authentication result: %s", authResult.getValue());
                                    },
                                    throwable -> {
                                        Timber.e(
                                                throwable,
                                                "Authentication failed: %s",
                                                throwable.getMessage());
                                        setAuthenticationResult(false);
                                    });

            disposables.add(disposable);
        } else {
            registrationFlag.setValue(true);
        }
    }

    private void setAuthenticationResult(boolean result) {
        isLoading.setValue(false);
        authResult.setValue(result);
    }

}
