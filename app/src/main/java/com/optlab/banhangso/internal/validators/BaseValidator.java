package com.optlab.banhangso.internal.validators;

import android.content.Context;
import androidx.annotation.NonNull;
import java.util.List;

public interface BaseValidator {

  Context getContext();

  /**
   * Default implementation for running validation rules
   *
   * @param value The value to validate
   * @param rules List of validation rules to apply
   * @return First error message found, or empty string if all rules pass
   */
  @NonNull default <T> String validate(T value, List<ValidationRule<T>> rules) {
    if (rules == null) return "";
    for (ValidationRule<T> rule : rules) {
      String result = rule.validate(value, getContext());
      if (!result.isEmpty()) return result;
    }
    return "";
  }
}
