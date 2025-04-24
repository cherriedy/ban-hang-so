package com.optlab.banhangso.ui.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginWithPhoneNumberViewModel extends ViewModel {
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> plainPassword = new MutableLiveData<>();

    @Inject
    public LoginWithPhoneNumberViewModel() {}

    public LiveData<String> getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String number) {
        phoneNumber.setValue(number);
    }

    public LiveData<String> getPlainPassword() {
        return plainPassword;
    }

    public void setPlainPassword(String password) {
        plainPassword.setValue(password);
    }
}
