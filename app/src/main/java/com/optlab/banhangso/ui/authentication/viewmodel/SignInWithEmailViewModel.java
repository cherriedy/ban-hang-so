package com.optlab.banhangso.ui.authentication.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.util.validator.AccountValidator;

import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import lombok.NonNull;

@HiltViewModel
public class SignInWithEmailViewModel extends ViewModel {
    private final AccountValidator validator;
    private final MutableLiveData<AuthData> authData = new MutableLiveData<>();
    private final MutableLiveData<AuthValidationState> validationState = new MutableLiveData<>();

    @Inject
    public SignInWithEmailViewModel(AccountValidator validator) {
        this.validator = validator;
        this.authData.setValue(new AuthData());
        this.validationState.setValue(new AuthValidationState(AuthValidationState.LOGIN_EMAIL));
    }

    public LiveData<AuthData> getAuthData() {
        return authData;
    }

    public LiveData<AuthValidationState> getValidationState() {
        return validationState;
    }

    private void updateValidationState(Consumer<AuthValidationState> action) {
        AuthValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state); // Apply the action to the current state
            validationState.setValue(state); // Update the LiveData with the new state
        }
    }

    public void validateEmail(@NonNull String email) {
        updateValidationState(state -> state.setEmailError(validator.validateEmail(email)));
    }

    public void validatePassword(@NonNull String password) {
        updateValidationState(
                state -> state.setPasswordError(validator.validatePassword(password, false)));
    }
}
