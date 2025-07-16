package com.optlab.banhangso.models.application;

import androidx.annotation.NonNull;
import lombok.Data;

@Data
public abstract class BaseFilter<T> {

  @NonNull protected Integer name;
  @NonNull protected T value;

  protected BaseFilter(@NonNull Integer name, @NonNull T value) {
    this.name = name;
    this.value = value;
  }
}
