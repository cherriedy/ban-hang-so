package com.optlab.banhangso.repositories;

import androidx.annotation.NonNull;
import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.UserFirebaseObject;
import com.optlab.banhangso.models.remote.mappers.UserFirebaseObjectMapper;
import com.optlab.banhangso.models.remote.requestes.SignUpRequest;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

public class AuthRepositoryImpl implements AuthRepository {

  private final FirebaseAuthService firebaseAuthService;
  private final AuthenticationService authenticationService;
  private final PreferencesRepositoryKt preferencesRepositoryKt;
  private final UserRepository userRepository;
  private final ErrorHandler errorHandler;

  public AuthRepositoryImpl(
      FirebaseAuthService firebaseAuthService,
      AuthenticationService authenticationService,
      PreferencesRepositoryKt preferencesRepositoryKt,
      UserRepository userRepository,
      ErrorHandler errorHandler) {
    this.firebaseAuthService = firebaseAuthService;
    this.authenticationService = authenticationService;
    this.preferencesRepositoryKt = preferencesRepositoryKt;
    this.userRepository = userRepository;
    this.errorHandler = errorHandler;
  }

  @Override
  public Single<Result<Void>> logInWithEmailAndPassword(
      @NonNull String email, @NonNull String password) {
    return firebaseAuthService
        .logInWithEmailAndPassword(email, password)
        .flatMap(this::getUserAndSetPreferences)
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Single<Result<Void>> signUpWithEmailAndPassword(@NonNull SignUpRequest signUpRequest) {
    return authenticationService
        .signUpWithEmailAndPassword(signUpRequest)
        .flatMap(
            response -> {
              if (response.isSuccess()) {
                return handleSignUpSuccess(response);
              } else {
                ApiResponseException throwable =
                    new ApiResponseException(response.message(), response.code());
                return Single.just(new Result.Failure<Void>(errorHandler.getError(throwable)));
              }
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Observable<Boolean> isAuthenticatedObservable() {
    return firebaseAuthService.isAuthenticatedObservable();
  }

  /**
   * Handles the success case of user sign-up by mapping the response to a User object and saving it
   * in preferences.
   *
   * @param response The response object containing user data.
   * @return A Single that emits a Result indicating success or failure.
   */
  private Single<Result<Void>> handleSignUpSuccess(@NonNull Response<UserFirebaseObject> response) {
    UserFirebaseObject userFirebaseObject = response.data();
    User user = UserFirebaseObjectMapper.toDomain(userFirebaseObject);
    return preferencesRepositoryKt
        .setUserRx(user)
        .map(unused -> (Result<Void>) new Result.Success<Void>(null))
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public boolean isAuthenticated() {
    return firebaseAuthService.isAuthenticated();
  }

  private Single<Result<Void>> getUserAndSetPreferences(@NonNull String userId) {
    return userRepository.getUser(userId).flatMap(this::handleUserResult);
  }

  private Single<Result<Void>> handleUserResult(Result<User> result) {
    if (result instanceof Result.Success) {
      User user = ((Result.Success<User>) result).getData();
      if (user != null) {
        // Save the user to preferences after successful login, and indicate that the user is
        // authenticated, by set `isAuthenticated` to true.
        return preferencesRepositoryKt
            .setUserRx(user)
            .doOnSubscribe(d -> Timber.d("Setting user preferences for: %s", user))
            .flatMap(
                success -> {
                  if (success) {
                    return preferencesRepositoryKt.setIsAuthenticatedRx(true);
                  } else {
                    return Single.just(false);
                  }
                })
            .map(__ -> (Result<Void>) new Result.Success<Void>(null))
            .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
      } else {
        return Single.just(
            new Result.Failure<>(
                errorHandler.getError(new IllegalStateException("User data is null"))));
      }
    } else if (result instanceof Result.Failure) {
      return Single.just(new Result.Failure<>(((Result.Failure<?>) result).getError()));
    } else {
      return Single.just(
          new Result.Failure<>(
              errorHandler.getError(new IllegalStateException("Unknown result type"))));
    }
  }

  @Override
  public Completable signOut() {
    return firebaseAuthService.signOut().andThen(preferencesRepositoryKt.clearPreferencesRx());
  }
}
