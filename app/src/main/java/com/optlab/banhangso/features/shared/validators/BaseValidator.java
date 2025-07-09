package com.optlab.banhangso.features.shared.validators;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.List;

public abstract class BaseValidator {

  protected final Context context;

  protected BaseValidator(Context context) {
    this.context = context;
  }

  /**
   * Default implementation for running validation rules
   *
   * @param value The value to validate
   * @param rules List of validation rules to apply
   * @return First error message found, or empty string if all rules pass
   */
  @NonNull protected <T> String validate(T value, List<ValidationRule<T>> rules) {
    if (rules == null) return "";
    for (ValidationRule<T> rule : rules) {
      String result = rule.validate(value, context);
      if (!result.isEmpty()) return result;
    }
    return "";
  }
}
