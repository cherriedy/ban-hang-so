package com.optlab.banhangso.features.shared.validators;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import org.jetbrains.annotations.Contract;

public final class ValidationRules {

  @NonNull @Contract(pure = true)
  public static ValidationRule<String> notEmpty(@StringRes int errorStringRes) {
    return (value, context) -> TextUtils.isEmpty(value) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<String> minLength(int minLength, int errorStringRes) {
    return (value, context) ->
        (value != null && value.length() < minLength) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<String> maxLength(int maxLength, int errorStringRes) {
    return (value, context) ->
        (value != null && value.length() > maxLength) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<String> regex(int regexStringRes, int errorStringRes) {
    return (value, context) -> {
      if (value != null && !value.isBlank() && !value.matches(context.getString(regexStringRes))) {
        return context.getString(errorStringRes);
      }
      return "";
    };
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<String> emailPattern(int errorStringRes) {
    return (value, context) -> {
      if (value != null && !value.isBlank() && !Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
        return context.getString(errorStringRes);
      }
      return "";
    };
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<Double> positiveNumber(int errorStringRes) {
    return (value, context) ->
        (value != null && value < 0) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<Double> nonZero(int errorStringRes) {
    return (value, context) ->
        (value != null && value.equals(0.0)) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static ValidationRule<Boolean> mustBeTrue(int errorStringRes) {
    return (value, context) -> (value != null && !value) ? context.getString(errorStringRes) : "";
  }

  @NonNull @Contract(pure = true)
  public static <T> ValidationRule<T> custom(@NonNull ConstraintValidator<T> logic) {
    return logic::validate;
  }

  @FunctionalInterface
  public interface ConstraintValidator<T> {
    String validate(T value, Context context);
  }
}
