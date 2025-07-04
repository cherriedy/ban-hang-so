package com.optlab.banhangso.internal.validators;

import static com.optlab.banhangso.internal.utilities.Constants.Customer.ERROR_EMAIL;
import static com.optlab.banhangso.internal.utilities.Constants.Customer.ERROR_NAME;
import static com.optlab.banhangso.internal.utilities.Constants.Customer.ERROR_PHONE;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerValidator extends BaseValidator {

  private HashMap<String, List<ValidationRule<String>>> rules = new HashMap<>();

  public CustomerValidator(Context context) {
    super(context);
    setupDefaultRules();
  }

  private void setupDefaultRules() {
    List<ValidationRule<String>> nameRules = new ArrayList<>();
    nameRules.add(ValidationRules.notEmpty(R.string.alter_customer_name_not_null));
    nameRules.add(ValidationRules.minLength(3, R.string.alter_customer_name_min_length));
    nameRules.add(ValidationRules.maxLength(50, R.string.alter_customer_name_max_len));
    rules.put(ERROR_NAME, nameRules);

    rules.put(ERROR_EMAIL, List.of(ValidationRules.emailPattern(R.string.error_invalid_email)));
    rules.put(
        ERROR_PHONE,
        List.of(
            ValidationRules.regex(
                R.string.regex_vietnam_phone_number, R.string.alert_phone_number_not_valid)));
  }

  @NonNull public String validateName(String name) {
    return validate(name, rules.get(ERROR_NAME));
  }

  @NonNull public String validateEmail(String email) {
    return validate(email, rules.get(ERROR_EMAIL));
  }

  @NonNull public String validatePhone(String phone) {
    return validate(phone, rules.get(ERROR_PHONE));
  }
}
