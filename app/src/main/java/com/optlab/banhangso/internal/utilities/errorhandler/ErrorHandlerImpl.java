package com.optlab.banhangso.internal.utilities.errorhandler;

import androidx.annotation.NonNull;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.exceptions.ApiResponseException;

public class ErrorHandlerImpl implements ErrorHandler {

    @Override
    public AppError getError(@NonNull Throwable throwable) {
        if (throwable instanceof FirebaseFirestoreException firestoreException) {
            return switch (firestoreException.getCode()) {
                case INVALID_ARGUMENT -> new AppError.InvalidArgument();
                case NOT_FOUND -> new AppError.NotFoundError();
                default -> new AppError.UnknownError();
            };
        }

        if (throwable instanceof ApiResponseException apiResponseException) {
            // Handle API exceptions with appropriate AppError types
            if (apiResponseException.getCode() == 404) {
                return new AppError.NotFoundError();
            } else {
                return new AppError.NetServiceError();
            }
        }

        return new AppError.UnknownError();
    }
}
