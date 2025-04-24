package com.optlab.banhangso.util.validator;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.optlab.banhangso.R;

public class AccountValidator {
    private final Context context;

    public AccountValidator(Context context) {
        this.context = context;
    }

    public String validatePhoneNumber(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            return context.getString(R.string.alert_phone_number_not_null);
        }
        if (!phoneNumber.matches(context.getString(R.string.regex_vietnam_phone_number))) {
            return context.getString(R.string.alert_phone_number_not_valid);
        }
        return "";
    }

    public String validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return context.getString(R.string.alert_email_not_null);
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return context.getString(R.string.alert_email_not_valid);
        }
        return "";
    }

    public String validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return context.getString(R.string.alert_password_not_null);
        }
        if (!password.matches(context.getString(R.string.regex_password))) {
            return context.getString(R.string.alert_password_not_valid);
        }
        return "";
    }
}
