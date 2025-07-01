package com.optlab.banhangso.internal.validators;

import android.content.Context;
import androidx.annotation.NonNull;

@FunctionalInterface
public interface ValidationRule<T> {
  /**
   * @param value The value to validate
   * @param context Android context for resource access
   * @return Error message if invalid, or empty string if valid
   */
  @NonNull String validate(T value, Context context);
}
