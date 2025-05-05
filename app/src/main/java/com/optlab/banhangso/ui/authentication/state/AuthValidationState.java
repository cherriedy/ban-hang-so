package com.optlab.banhangso.ui.authentication.state;

import android.text.TextUtils;

import lombok.NonNull;

/**
 * @noinspection LombokGetterMayBeUsed, LombokSetterMayBeUsed
 */
public class AuthValidationState {
    public static final String LOGIN_PHONE = "LOGIN_PHONE";
    public static final String LOGIN_EMAIL = "LOGIN_EMAIL";
    public static final String SIGNUP_PHONE = "SIGNUP_PHONE";
    public static final String SIGNUP_EMAIL = "SIGNUP_EMAIL";
    public static final String FORGET_PASSWORD_PHONE = "FORGET_PASSWORD_PHONE";
    public static final String FORGET_PASSWORD_EMAIL = "FORGET_PASSWORD_EMAIL";

    @NonNull private final String type;

    private String emailError;
    private String phoneError;
    private String passwordError;
    private String confirmPasswordError;
    private String otpError;
    private boolean hasNoError;

    public AuthValidationState(@NonNull String type) {
        this.type = type;
    }

    public void validateFields() {
        switch (type) {
            case LOGIN_PHONE ->
                    hasNoError = TextUtils.isEmpty(phoneError) && TextUtils.isEmpty(passwordError);
            case LOGIN_EMAIL ->
                    hasNoError = TextUtils.isEmpty(emailError) && TextUtils.isEmpty(passwordError);
            case FORGET_PASSWORD_PHONE ->
                    hasNoError = TextUtils.isEmpty(phoneError) && TextUtils.isEmpty(otpError);
            case FORGET_PASSWORD_EMAIL ->
                    hasNoError = TextUtils.isEmpty(emailError) && TextUtils.isEmpty(otpError);
            case SIGNUP_PHONE ->
                    hasNoError =
                            TextUtils.isEmpty(phoneError)
                                    && TextUtils.isEmpty(passwordError)
                                    && TextUtils.isEmpty(confirmPasswordError);
            case SIGNUP_EMAIL ->
                    hasNoError =
                            TextUtils.isEmpty(emailError)
                                    && TextUtils.isEmpty(passwordError)
                                    && TextUtils.isEmpty(confirmPasswordError);
            default -> throw new IllegalStateException("Invalid type: " + type);
        }
    }

    public @NonNull String getType() {
        return type;
    }

    public String getEmailError() {
        return emailError;
    }

    public void setEmailError(String emailError) {
        this.emailError = emailError;
        areFieldsValid();
    }

    private void areFieldsValid() {
        hasNoError =
                TextUtils.isEmpty(emailError)
                        && TextUtils.isEmpty(phoneError)
                        && TextUtils.isEmpty(passwordError)
                        && TextUtils.isEmpty(confirmPasswordError)
                        && TextUtils.isEmpty(otpError);
    }

    public String getPhoneError() {
        return phoneError;
    }

    public void setPhoneError(String phoneError) {
        this.phoneError = phoneError;
        areFieldsValid();
    }

    public String getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
        areFieldsValid();
    }

    public String getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public void setConfirmPasswordError(String confirmPasswordError) {
        this.confirmPasswordError = confirmPasswordError;
        areFieldsValid();
    }

    public String getOtpError() {
        return otpError;
    }

    public void setOtpError(String otpError) {
        this.otpError = otpError;
        areFieldsValid();
    }

    public void setHasNoError(boolean hasNoError) {
        this.hasNoError = hasNoError;
    }

    public boolean isHasNoError() {
        return hasNoError;
    }
}
