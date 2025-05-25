package com.optlab.banhangso.ui.main.authentication.viewmodel;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.repository.UserRepository;
import com.optlab.banhangso.domain.util.AuthData;
import com.optlab.banhangso.domain.util.Resource;
import com.optlab.banhangso.ui.main.authentication.state.AuthValidationState;
import com.optlab.banhangso.util.validator.AuthValidator;

import dagger.hilt.android.lifecycle.HiltViewModel;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import timber.log.Timber;

import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class AuthenticationViewModel extends ViewModel {
    private static final String KEY_IS_SIGN_IN = "is_sign_in";

    private final SavedStateHandle savedStateHandle;
    private final AuthValidator validator;
    private final Observer<Object> signInObserver;
    private final FirebaseAuth firebaseAuth;
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final MutableLiveData<AuthData> authData = new MutableLiveData<>(new AuthData());
    private final MutableLiveData<AuthValidationState> validationState =
            new MutableLiveData<>(new AuthValidationState(AuthValidationState.SIGN_IN_EMAIL));
    private final MutableLiveData<Boolean> isAuthenticating = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> authenticateResult = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> shouldNavigateToSignUp = new MutableLiveData<>(false);

    private Observable.OnPropertyChangedCallback authDataPropertyChangedCallback;

    @Inject
    public AuthenticationViewModel(
            SavedStateHandle savedStateHandle,
            AuthValidator validator,
            FirebaseAuth firebaseAuth,
            PreferenceRepository preferenceRepository,
            UserRepository userRepository) {
        this.savedStateHandle = savedStateHandle;
        this.validator = validator;
        this.firebaseAuth = firebaseAuth;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;

        signInObserver =
                isSignIn -> {
                    if (isSignIn instanceof Boolean && (Boolean) isSignIn) {
                        setValidateFields(AuthValidationState.SIGN_IN_EMAIL);
                    } else {
                        setValidateFields(AuthValidationState.SIGN_UP_EMAIL);
                    }
                };

        initSignInState();
        observeAuthChanges();
        observeSignInState();
    }

    private void observeAuthChanges() {
        AuthData authData = this.authData.getValue();
        if (authData != null && authDataPropertyChangedCallback == null) {
            authDataPropertyChangedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            switch (propertyId) {
                                case BR.email -> validateEmail();
                                case BR.password -> validatePassword();
                                case BR.confirmPassword -> {
                                    if (Boolean.FALSE.equals(getIsSignIn().getValue())) {
                                        validateConfirmPassword();
                                    }
                                }
                            }
                        }
                    };
            authData.addOnPropertyChangedCallback(authDataPropertyChangedCallback);
        }
    }

    @Override
    protected void onCleared() {
        authData.setValue(null);
        validationState.setValue(null);

        savedStateHandle.getLiveData(KEY_IS_SIGN_IN).removeObserver(signInObserver);
        savedStateHandle.remove(KEY_IS_SIGN_IN);

        if (authDataPropertyChangedCallback != null) {
            authDataPropertyChangedCallback = null;
        }

        disposable.clear();
        super.onCleared();
    }

    /**
     * Observe sign-in state changes. Uses observeForever, so always remove observer in onCleared.
     */
    private void observeSignInState() {
        savedStateHandle.getLiveData(KEY_IS_SIGN_IN).observeForever(signInObserver);
    }

    /** Initializes sign-in state to true if not set. */
    private void initSignInState() {
        Boolean isSignIn = savedStateHandle.get(KEY_IS_SIGN_IN);
        savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn != null ? isSignIn : true);
    }

    public LiveData<AuthData> getAuthData() {
        return authData;
    }

    public LiveData<AuthValidationState> getValidationState() {
        return validationState;
    }

    public LiveData<Boolean> getIsSignIn() {
        return savedStateHandle.getLiveData(KEY_IS_SIGN_IN);
    }

    public void setIsSignIn(boolean isSignIn) {
        savedStateHandle.set(KEY_IS_SIGN_IN, isSignIn);
    }

    public LiveData<Boolean> getIsAuthenticating() {
        return isAuthenticating;
    }

    public LiveData<Boolean> getAuthenticateResult() {
        return authenticateResult;
    }

    public LiveData<Boolean> getShouldNavigateToSignUp() {
        return shouldNavigateToSignUp;
    }

    public void setShouldNavigateToSignUp(boolean shouldNavigate) {
        shouldNavigateToSignUp.setValue(shouldNavigate);
    }

    private void updateValidationState(Consumer<AuthValidationState> action) {
        AuthValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state); // Apply the action to the current state
            validationState.setValue(state); // Update the LiveData with the new state
        }
    }

    public void setValidateFields(String fields) {
        updateValidationState(state -> state.validateFields(fields));
    }

    public void validateEmail() {
        updateValidationState(
                state -> {
                    AuthData authData = this.authData.getValue();
                    if (authData != null) {
                        state.setEmailError(validator.validateEmail(authData.getEmail()));
                    }
                });
    }

    public void validatePassword() {
        updateValidationState(
                state -> {
                    AuthData authData = this.authData.getValue();
                    if (authData != null) {
                        state.setPasswordError(
                                validator.validatePassword(authData.getPassword(), false));
                    }
                });
    }

    public void validateConfirmPassword() {
        updateValidationState(
                state -> {
                    AuthData authData = this.authData.getValue();
                    if (authData != null) {
                        state.setConfirmPasswordError(
                                validator.validateConfirmPassword(
                                        authData.getPassword(), authData.getConfirmPassword()));
                    }
                });
    }

    /**
     * @noinspection unused
     */
    @SuppressLint("CheckResult")
    public void onAuthenticateButtonClick(View view) {
        AuthData authData = this.authData.getValue();
        if (authData == null) {
            Timber.e("Authentication data is null");
            return;
        }

        if (Boolean.TRUE.equals(getIsSignIn().getValue())) {
            isAuthenticating.setValue(true);
            firebaseAuth
                    .signInWithEmailAndPassword(authData.getEmail(), authData.getPassword())
                    .addOnSuccessListener(
                            authResult -> {
                                FirebaseUser firebaseUser = authResult.getUser();
                                if (firebaseUser == null) {
                                    Timber.e("Sign-in failed: FirebaseUser is null");
                                    return;
                                }

                                disposable.add(
                                        userRepository
                                                .getUserById(firebaseUser.getUid())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .filter(Resource::isLoaded)
                                                .doOnSuccess(this::handleUserCache)
                                                .subscribe());
                            })
                    .addOnFailureListener(e -> {
                        Timber.e(e, "Sign-in failed: %s", e.getMessage());
                        setAuthenticationResult(false);
                    });
        } else {
            shouldNavigateToSignUp.setValue(true);
        }
    }

    private void handleUserCache(Resource<User> resource) {
        if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
            User currentUser = resource.data;
            setAuthenticationResult(true);
            preferenceRepository.setAuthenticatedUser(currentUser);
        } else {
            setAuthenticationResult(false);
            Timber.e("Failed to retrieve user data: %s", resource.message);
        }
    }

    private void setAuthenticationResult(boolean result) {
        isAuthenticating.setValue(false);
        authenticateResult.setValue(result);
    }
}
