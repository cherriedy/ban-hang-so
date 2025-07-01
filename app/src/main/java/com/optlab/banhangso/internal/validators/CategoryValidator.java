package com.optlab.banhangso.internal.validators;

import android.content.Context;
import androidx.annotation.NonNull;
import com.optlab.banhangso.R;
import java.util.ArrayList;
import java.util.List;

public final class CategoryValidator implements BaseValidator {

  private final Context context;
  private final List<ValidationRule<String>> nameRules = new ArrayList<>();

  public CategoryValidator(Context context) {
    this.context = context;
    setupDefaultNameRules();
  }

  @Override
  public Context getContext() {
    return context;
  }

  private void setupDefaultNameRules() {
    nameRules.add(ValidationRules.notEmpty(R.string.alert_category_name_not_null));
    nameRules.add(ValidationRules.minLength(3, R.string.alert_category_min_chars));
    nameRules.add(ValidationRules.maxLength(50, R.string.alert_category_max_chars));
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
