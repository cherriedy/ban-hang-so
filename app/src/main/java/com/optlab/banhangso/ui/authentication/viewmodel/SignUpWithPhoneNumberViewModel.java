package com.optlab.banhangso.ui.authentication.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class SignUpWithPhoneNumberViewModel extends ViewModel {
    // private final AccountValidator validator;
    private final MutableLiveData<AuthData> authData = new MutableLiveData<>();
    private final MutableLiveData<AuthValidationState> validationState = new MutableLiveData<>();

    @Inject
    public SignUpWithPhoneNumberViewModel() {
        authData.setValue(new AuthData());
        validationState.setValue(new AuthValidationState(AuthValidationState.SIGNUP_PHONE));
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
            action.accept(state);
            validationState.setValue(state);
        }
    }

    public void validatePhone(@NonNull String phone) {
        // updateValidationState(state -> state.setPhoneError(validator.validatePhoneNumber(phone)));
    }
}
