package com.optlab.banhangso.features.main.brand;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.shared.validators.BaseValidator;
import com.optlab.banhangso.features.shared.validators.ValidationRule;
import com.optlab.banhangso.features.shared.validators.ValidationRules;
import java.util.ArrayList;
import java.util.List;

public final class BrandValidator extends BaseValidator {

  private final List<ValidationRule<String>> nameRules = new ArrayList<>();

  public BrandValidator(Context context) {
    super(context);
    setupDefaultNameRules();
  }

  private void setupDefaultNameRules() {
    nameRules.add(ValidationRules.notEmpty(R.string.alter_brand_name_non_null));
    nameRules.add(ValidationRules.minLength(3, R.string.alter_brand_name_min_chars));
  }

  public void addNameRule(@NonNull ValidationRule<String> rule) {
    nameRules.add(rule);
  }

  public void clearNameRules() {
    nameRules.clear();
  }

  public void setCustomNameRules(List<ValidationRule<String>> rules) {
    nameRules.clear();
    nameRules.addAll(rules);
  }

  @NonNull public String validateName(String name) {
    return validate(name, nameRules);
  }
}
