package com.optlab.banhangso.models.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public abstract sealed class Result<T> permits Result.Success, Result.Failure {

    /**
     * Success result with data
     *
     * @param <T> Type of the data
     */
    public static final class Success<T> extends Result<T> {
        @Nullable private final T data;

        public Success(@Nullable T data) {
            this.data = data;
        }

        @Nullable public T getData() {
            return data;
        }

        @NonNull @Override
        public String toString() {
            return "Success[data=" + (data != null ? data.toString() : "null") + "]";
        }
    }

    /**
     * Failure result with error
     *
     * @param <T> Type of the data that would have been returned on success
     */
    public static final class Failure<T> extends Result<T> {
        @NonNull private final AppError error;

        public Failure(@NonNull AppError error) {
            this.error = error;
        }

        @NonNull public AppError getError() {
            return error;
        }

        @NonNull @Override
        public String toString() {
            return "Failure[error=" + error + "]";
        }
    }
}
