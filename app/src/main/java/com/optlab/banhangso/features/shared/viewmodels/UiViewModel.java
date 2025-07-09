package com.optlab.banhangso.features.shared.viewmodels;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @noinspection LombokGetterMayBeUsed
 */
public abstract class UiViewModel<T> extends RxViewModel {

  protected final MutableLiveData<T> uiModel = new MutableLiveData<>();
  protected final ObservableMap<String, String> errors = new ObservableArrayMap<>();

  public LiveData<T> getUiModel() {
    return uiModel;
  }

  public void setUiModel(@NonNull T value) {
    uiModel.setValue(value);
  }

  public ObservableMap<String, String> getErrors() {
    return this.errors;
  }

  /**
   * Validates a field and updates the error map accordingly
   *
   * @param fieldKey The key for the field in the error map
   * @param fieldValue The value to validate
   * @param validator Function that takes the field value and returns an error message (empty if
   *     valid)
   * @param <V> The type of the field value
   */
  private <V> void processValidationField(
      @NonNull String fieldKey, V fieldValue, @NonNull Function<V, String> validator) {
    String errorMessage = validator.apply(fieldValue);
    if (errorMessage == null || errorMessage.isBlank()) {
      errors.remove(fieldKey);
    } else {
      errors.put(fieldKey, errorMessage);
    }
  }

  /**
   * Validates a field using the current UI model
   *
   * @param fieldKey The key for the field in the error map
   * @param fieldExtractor Function to extract the field value from the UI model
   * @param validator Function that validates the field value and returns an error message
   * @param <V> The type of the field value
   */
  protected <V> void validateField(
      @NonNull String fieldKey,
      @NonNull Function<T, V> fieldExtractor,
      @NonNull Function<V, String> validator) {
    T model = uiModel.getValue();
    if (model != null) {
      V fieldValue = fieldExtractor.apply(model);
      processValidationField(fieldKey, fieldValue, validator);
    }
    onValidationComplete();
  }

  /**
   * Validates a field using multiple values from the current UI model
   *
   * @param fieldKey The key for the field in the error map
   * @param validator Function that takes extracted values and returns an error message
   * @param extractors Variable number of field extractors to get values from the UI model
   */
  @SafeVarargs
  protected final void validateField(
      @NonNull String fieldKey,
      @NonNull MultiFieldValidator validator,
      @NonNull Function<T, ?>... extractors) {
    T model = uiModel.getValue(); // get the current UI model
    if (model != null) {
      // Extract values using the provided extractors
      Object[] values = new Object[extractors.length];
      for (int i = 0; i < extractors.length; i++) {
        values[i] = extractors[i].apply(model);
      }

      // Validate the extracted values, using the provided validator
      String errorMessage = validator.validate(values);
      if (errorMessage == null || errorMessage.isBlank()) {
        errors.remove(fieldKey);
      } else {
        errors.put(fieldKey, errorMessage);
      }
    }
    onValidationComplete();
  }

  /**
   * Functional interface for multi-field validation functions Takes an array of extracted field
   * values and returns an error message
   */
  @FunctionalInterface
  public interface MultiFieldValidator {
    /**
     * Validates multiple field values
     *
     * @param values Array of field values extracted from the UI model
     * @return Error message if validation fails, empty string or null if valid
     */
    String validate(Object... values);
  }

  /** Validates a field using two values from the current UI model */
  protected <V1, V2> void validateField(
      @NonNull String fieldKey,
      @NonNull Function<T, V1> firstExtractor,
      @NonNull Function<T, V2> secondExtractor,
      @NonNull BiFunction<V1, V2, String> validator) {
    validateField(
        fieldKey,
        values -> {
          @SuppressWarnings("unchecked")
          V1 first = (V1) values[0];
          @SuppressWarnings("unchecked")
          V2 second = (V2) values[1];
          return validator.apply(first, second);
        },
        firstExtractor,
        secondExtractor);
  }

  /** Validates a field using three values from the current UI model */
  protected <V1, V2, V3> void validateField(
      @NonNull String fieldKey,
      @NonNull Function<T, V1> firstExtractor,
      @NonNull Function<T, V2> secondExtractor,
      @NonNull Function<T, V3> thirdExtractor,
      @NonNull TriFunction<V1, V2, V3, String> validator) {
    validateField(
        fieldKey,
        values -> {
          @SuppressWarnings("unchecked")
          V1 first = (V1) values[0];
          @SuppressWarnings("unchecked")
          V2 second = (V2) values[1];
          @SuppressWarnings("unchecked")
          V3 third = (V3) values[2];
          return validator.apply(first, second, third);
        },
        firstExtractor,
        secondExtractor,
        thirdExtractor);
  }

  /** Functional interface for three-parameter validation functions */
  @FunctionalInterface
  public interface TriFunction<T, U, V, R> {
    R apply(T t, U u, V v);
  }

  /**
   * Called after each validation to allow subclasses to update their state Override this method to
   * implement custom logic after validation
   */
  protected void onValidationComplete() {}
}
