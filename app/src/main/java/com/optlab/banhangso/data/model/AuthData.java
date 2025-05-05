package com.optlab.banhangso.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.optlab.banhangso.BR;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * AuthData class represents the authentication data for a user. It includes fields for email, phone
 * number, password, confirm password, and OTP. The class extends BaseObservable to support data
 * binding in Android.
 */
@AllArgsConstructor
@NoArgsConstructor
public class AuthData extends BaseObservable {
    private String email;
    private String phoneNumber;
    private String password;
    private String confirmPassword;
    private String otp;

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        notifyPropertyChanged(BR.phoneNumber);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        notifyPropertyChanged(BR.confirmPassword);
    }

    @Bindable
    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
        notifyPropertyChanged(BR.otp);
    }
}
