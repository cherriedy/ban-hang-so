package com.optlab.banhangso.features.main.authentication;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.shared.validators.BaseValidator;
import com.optlab.banhangso.features.shared.validators.ValidationRule;
import com.optlab.banhangso.features.shared.validators.ValidationRules;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AuthValidator extends BaseValidator {

  private final Map<String, List<ValidationRule<String>>> stringRules = new HashMap<>();
  private final List<ValidationRule<Boolean>> termsRules = new ArrayList<>();

  public AuthValidator(Context context) {
    super(context);
    setupDefaultRules();
  }

  private void setupDefaultRules() {
    // Phone number rules
    List<ValidationRule<String>> phoneRules = new ArrayList<>();
    phoneRules.add(
        ValidationRules.regex(
            R.string.regex_vietnam_phone_number, R.string.alert_phone_number_not_valid));
    stringRules.put("phone", phoneRules);

    // Email rules
    List<ValidationRule<String>> emailRules = new ArrayList<>();
    emailRules.add(ValidationRules.notEmpty(R.string.alert_email_not_null));
    emailRules.add(ValidationRules.emailPattern(R.string.alert_email_not_valid));
    stringRules.put("email", emailRules);

    // Password rules
    List<ValidationRule<String>> passwordRules = new ArrayList<>();
    passwordRules.add(ValidationRules.notEmpty(R.string.alert_password_not_null));
    stringRules.put("password", passwordRules);

    // Store name rules
    List<ValidationRule<String>> storeNameRules = new ArrayList<>();
    storeNameRules.add(ValidationRules.notEmpty(R.string.alert_store_name_not_null));
    storeNameRules.add(ValidationRules.minLength(3, R.string.alert_store_name_min_chars));
    storeNameRules.add(ValidationRules.maxLength(50, R.string.alert_store_name_max_chars));
    stringRules.put("storeName", storeNameRules);

    // Store code rules
    List<ValidationRule<String>> storeCodeRules = new ArrayList<>();
    storeCodeRules.add(ValidationRules.notEmpty(R.string.alert_store_code_not_null));
    stringRules.put("storeCode", storeCodeRules);

    // Store description rules
    List<ValidationRule<String>> storeDescRules = new ArrayList<>();
    storeDescRules.add(ValidationRules.notEmpty(R.string.alert_store_description_not_null));
    storeDescRules.add(ValidationRules.minLength(3, R.string.alert_store_description_min_chars));
    storeDescRules.add(ValidationRules.maxLength(100, R.string.alert_store_description_max_chars));
    stringRules.put("storeDescription", storeDescRules);

    // Contact name rules
    List<ValidationRule<String>> contactNameRules = new ArrayList<>();
    contactNameRules.add(ValidationRules.notEmpty(R.string.alert_contact_name_not_null));
    contactNameRules.add(ValidationRules.minLength(3, R.string.alert_contact_name_min_chars));
    contactNameRules.add(ValidationRules.maxLength(50, R.string.alert_contact_name_max_chars));
    stringRules.put("contactName", contactNameRules);

    // Terms and conditions rules
    termsRules.add(ValidationRules.mustBeTrue(R.string.alert_agree_terms_and_conditions));
  }

  // Rule management methods
  public void addRuleForField(String fieldName, @NonNull ValidationRule<String> rule) {
    stringRules.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(rule);
  }

  public void setCustomRulesForField(String fieldName, List<ValidationRule<String>> rules) {
    stringRules.put(fieldName, new ArrayList<>(rules));
  }

  public void addTermsRule(@NonNull ValidationRule<Boolean> rule) {
    termsRules.add(rule);
  }

  // Validation methods
  @NonNull public String validatePhoneNumber(@NonNull String phoneNumber) {
    return validate(phoneNumber, stringRules.get("phone"));
  }

  @NonNull public String validateEmail(String email) {
    return validate(email, stringRules.get("email"));
  }

  @NonNull public String validatePassword(String password, boolean regexRequired) {
    List<ValidationRule<String>> rules = new ArrayList<>(stringRules.get("password"));
    if (regexRequired) {
      rules.add(ValidationRules.regex(R.string.regex_password, R.string.alert_password_not_valid));
    }
    return validate(password, rules);
  }

  @NonNull public String validateConfirmPassword(String password, String confirmPassword) {
    List<ValidationRule<String>> rules = new ArrayList<>();
    rules.add(ValidationRules.notEmpty(R.string.alert_confirm_password_not_null));
    rules.add(
        (value, context) ->
            !value.equals(password)
                ? context.getString(R.string.alert_confirm_password_not_match)
                : "");
    return validate(confirmPassword, rules);
  }

  @NonNull public String validateStoreName(String name) {
    return validate(name, stringRules.get("storeName"));
  }

  @NonNull public String validateStoreCode(String code) {
    return validate(code, stringRules.get("storeCode"));
  }

  @NonNull public String validateStoreDescription(String description) {
    return validate(description, stringRules.get("storeDescription"));
  }

  @NonNull public String validateContactName(String name) {
    return validate(name, stringRules.get("contactName"));
  }

  @NonNull public String validateAgreeTermsAndConditions(boolean isChecked) {
    return validate(isChecked, termsRules);
  }
}
