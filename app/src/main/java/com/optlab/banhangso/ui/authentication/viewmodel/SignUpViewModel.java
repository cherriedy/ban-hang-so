package com.optlab.banhangso.ui.authentication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.data.model.User;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class SignUpViewModel extends ViewModel {
    // private final AccountValidator validator;
    private final MutableLiveData<AuthData> authData = new MutableLiveData<>();
    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<AuthValidationState> validationState = new MutableLiveData<>();

    @Inject
    public SignUpViewModel() {
        authData.setValue(new AuthData());
    }

    public LiveData<AuthData> getAuthData() {
        return authData;
    }

    public LiveData<User> getUser() {
        return user;
    }

    public void setUser(@NonNull User newUser) {
        user.setValue(newUser);
    }

    public void setAuthValidationState(@NonNull AuthValidationState newState) {
        validationState.setValue(newState);
    }

    public LiveData<AuthValidationState> getValidationState() {
        return validationState;
    }

    private void updateValidationState(Consumer<AuthValidationState> action) {
        AuthValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state);
            validationState.setValue(state);
        }
    }

    public void validatePhone(@NonNull String phone) {
        // updateValidationState(state ->
        // state.setPhoneError(validator.validatePhoneNumber(phone)));
    }
}
