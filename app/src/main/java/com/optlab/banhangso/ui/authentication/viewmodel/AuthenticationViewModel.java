package com.optlab.banhangso.ui.authentication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.app.AuthData;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.util.validator.AuthValidator;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

@HiltViewModel
public class AuthenticationViewModel extends ViewModel {
    private static final String KEY_IS_SIGN_IN = "is_sign_in";

    private final SavedStateHandle savedStateHandle;
    private final AuthValidator validator;
    private final MutableLiveData<AuthData> authData = new MutableLiveData<>();
    private final MutableLiveData<AuthValidationState> validationState = new MutableLiveData<>();

    @Inject
    public AuthenticationViewModel(SavedStateHandle savedStateHandle, AuthValidator validator) {
        this.savedStateHandle = savedStateHandle;
        this.validator = validator;
        this.authData.setValue(new AuthData());
        this.validationState.setValue(new AuthValidationState(AuthValidationState.SIGN_IN_EMAIL));

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

    private void updateValidationState(Consumer<AuthValidationState> action) {
        AuthValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state); // Apply the action to the current state
            validationState.setValue(state); // Update the LiveData with the new state
        }
    }

    public void setValidateFields(String fields) {
        Timber.e("setValidateFields: %s", fields);
        updateValidationState(state -> state.validateFields(fields));
    }

    public void validateEmail(@NonNull String email) {
        updateValidationState(state -> state.setEmailError(validator.validateEmail(email)));
    }

    public void validatePassword(@NonNull String password) {
        updateValidationState(
                state -> state.setPasswordError(validator.validatePassword(password, false)));
    }

    public void validateConfirmPassword(@NonNull String password, @NonNull String confirmPassword) {
        updateValidationState(
                state ->
                        state.setConfirmPasswordError(
                                validator.validateConfirmPassword(password, confirmPassword)));
    }
}
