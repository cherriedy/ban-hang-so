package com.optlab.banhangso.internal.utilities.errorhandler;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import timber.log.Timber;

public class ErrorHandlerImpl implements ErrorHandler {

  @Override
  public AppError getError(@NonNull Throwable throwable) {
    Timber.e("Original exception: %s", throwable.getMessage());

    //    if (throwable instanceof FirebaseFirestoreException firestoreException) {
    //      return switch (firestoreException.getCode()) {
    //        case INVALID_ARGUMENT -> new AppError.InvalidArgument();
    //        case NOT_FOUND -> new AppError.NotFoundError();
    //        default -> new AppError.UnknownError();
    //      };
    //    }

    if (throwable instanceof ApiResponseException apiResponseException) {
      return switch (apiResponseException.getCode()) {
        case 404 -> new AppError.NotFoundError();
        case 201 -> new AppError.InvalidArgument();
        case 409 -> new AppError.DuplicateError();
        case 403 -> new AppError.ForbiddenError();
        default -> new AppError.UnknownError();
      };
    }

    return new AppError.UnknownError();
  }
}
