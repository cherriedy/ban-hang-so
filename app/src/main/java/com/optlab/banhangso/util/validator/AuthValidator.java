package com.optlab.banhangso.util.validator;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.optlab.banhangso.R;

public final class AuthValidator {
    private final Context context;

    public AuthValidator(Context context) {
        this.context = context;
    }

    public String validatePhoneNumber(String phoneNumber) {
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

    public String validatePassword(String password, boolean regexRequired) {
        if (TextUtils.isEmpty(password)) {
            return context.getString(R.string.alert_password_not_null);
        }
        if (!password.matches(context.getString(R.string.regex_password)) && regexRequired) {
            return context.getString(R.string.alert_password_not_valid);
        }
        return "";
    }

    public String validateConfirmPassword(String password, String confirmPassword) {
        if (TextUtils.isEmpty(confirmPassword)) {
            return context.getString(R.string.alert_confirm_password_not_null);
        }
        if (!confirmPassword.equals(password)) {
            return context.getString(R.string.alert_confirm_password_not_match);
        }
        return "";
    }

    public String validateStoreName(String name) {
        if (TextUtils.isEmpty(name)) {
            return context.getString(R.string.alert_store_name_not_null);
        }
        if (name.length() < 3) {
            return context.getString(R.string.alert_store_name_min_chars);
        }
        if (name.length() > 50) {
            return context.getString(R.string.alert_store_name_max_chars);
        }
        return "";
    }

    public String validateStoreCode(String code) {
        if (TextUtils.isEmpty(code)) {
            return context.getString(R.string.alert_store_code_not_null);
        }
        return "";
    }

    public String validateStoreDescription(String description) {
        if (TextUtils.isEmpty(description)) {
            return context.getString(R.string.alert_store_description_not_null);
        }
        if (description.length() < 3) {
            return context.getString(R.string.alert_store_description_min_chars);
        }
        if (description.length() > 100) {
            return context.getString(R.string.alert_store_description_max_chars);
        }
        return "";
    }

    public String validateContactName(String name) {
        if (TextUtils.isEmpty(name)) {
            return context.getString(R.string.alert_contact_name_not_null);
        }
        if (name.length() < 3) {
            return context.getString(R.string.alert_contact_name_min_chars);
        }
        if (name.length() > 50) {
            return context.getString(R.string.alert_contact_name_max_chars);
        }
        return "";
    }

    public String validateAgreeTermsAndConditions(boolean isChecked) {
        if (!isChecked) {
            return context.getString(R.string.alert_agree_terms_and_conditions);
        }
        return "";
    }
}
