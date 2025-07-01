package com.optlab.banhangso.internal.validators;

import android.content.Context;
import com.optlab.banhangso.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

public class StaffValidator implements BaseValidator {

  private final Context context;
  private final Map<String, List<ValidationRule<String>>> rules = new HashMap<>();

  public StaffValidator(Context context) {
    this.context = context;
    setupDefaultRules();
  }

  @Override
  public Context getContext() {
    return context;
  }

  private void setupDefaultRules() {
    List<ValidationRule<String>> nameRules = new ArrayList<>();
    nameRules.add(ValidationRules.notEmpty(R.string.error_staff_non_null));
    nameRules.add(ValidationRules.minLength(3, R.string.error_staff_name_min_length));
    nameRules.add(ValidationRules.maxLength(50, R.string.error_staff_name_max_length));
    rules.put("name", nameRules);

    List<ValidationRule<String>> phoneRules = new ArrayList<>();
    phoneRules.add(
        ValidationRules.regex(
            R.string.regex_vietnam_phone_number, R.string.alert_phone_number_not_valid));
    rules.put("phone", phoneRules);

    rules.put("email", List.of(ValidationRules.emailPattern(R.string.error_invalid_email)));
  }

  @NonNull public String validateStaffName(String name) {
    return validate(name, rules.get("name"));
  }

  @NonNull public String validateEmail(String email) {
    return validate(email, rules.get("email"));
  }

  @NonNull public String validatePhone(String phone) {
    return validate(phone, rules.get("phone"));
  }
}
