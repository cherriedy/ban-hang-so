package com.optlab.banhangso.internal.utilities.errorhandler;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.AppError;

public interface ErrorHandler {
    public AppError getError(@NonNull Throwable throwable);
}
