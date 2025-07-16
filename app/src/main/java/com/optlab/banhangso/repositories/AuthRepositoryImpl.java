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
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.services.interfaces.AuthenticationService;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import java.util.concurrent.atomic.AtomicReference;
import timber.log.Timber;

public class AuthRepositoryImpl implements AuthRepository {

  private final FirebaseAuthService firebaseAuthService;
  private final AuthenticationService authenticationService;
  private final PreferencesRepository preferencesRepository;
  private final UserRepository userRepository;
  private final ErrorHandler errorHandler;
  private final AtomicReference<User> activeUser = new AtomicReference<>();

  public AuthRepositoryImpl(
      FirebaseAuthService firebaseAuthService,
      AuthenticationService authenticationService,
      PreferencesRepository preferencesRepository,
      UserRepository userRepository,
      ErrorHandler errorHandler) {
    this.firebaseAuthService = firebaseAuthService;
    this.authenticationService = authenticationService;
    this.preferencesRepository = preferencesRepository;
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
                Throwable throwable = new ApiResponseException(response.message(), response.code());
                return Single.just(new Result.Failure<Void>(errorHandler.getError(throwable)));
              }
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
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
    return preferencesRepository
        .setUser(user)
        .toSingleDefault((Result<Void>) new Result.Success<Void>(null))
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Observable<Boolean> isAuthenticated() {
    return firebaseAuthService.isAuthenticated();
  }

  private Single<Result<Void>> getUserAndSetPreferences(@NonNull String userId) {
    return userRepository.getUser(userId).flatMap(this::handleUserResult);
  }

  private Single<Result<Void>> handleUserResult(Result<User> userResult) {
    if (userResult instanceof Result.Success<User> success) {
      User user = success.getData();
      if (user != null) {
        Timber.d("Cache user in memory: %s", user);
        activeUser.set(user);
        return preferencesRepository
            .setUser(user)
            .doOnSubscribe(__ -> Timber.d("Setting user preferences for: %s", user))
            .toSingleDefault((Result<Void>) new Result.Success<Void>(null))
            .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
      } else {
        return Single.just(
            new Result.Failure<>(
                errorHandler.getError(new IllegalStateException("User data is null"))));
      }
    } else if (userResult instanceof Result.Failure<User> failure) {
      return Single.just(new Result.Failure<>(failure.getError()));
    }

    return Single.just(
        new Result.Failure<>(
            errorHandler.getError(new IllegalStateException("Unknown result type"))));
  }

  @Override
  public Single<Result<User>> getUser() {
    // Check if user is already cached in memory
    User cachedUser = activeUser.get();
    if (cachedUser != null) {
      return Single.just(new Result.Success<>(cachedUser));
    }

    // If not in memory, get from preferences and cache it
    return preferencesRepository
        .getUser()
        .map(
            user -> {
              activeUser.set(user);
              return (Result<User>) new Result.Success<>(user);
            })
        .switchIfEmpty(
            Single.just(
                new Result.Failure<>(
                    errorHandler.getError(
                        new IllegalStateException("No user found in preferences")))))
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Completable setUser(@NonNull User user) {
    return preferencesRepository.setUser(user).doOnComplete(() -> activeUser.set(user));
  }

  @Override
  public Completable signOut() {
    return firebaseAuthService
        .signOut()
        .andThen(preferencesRepository.clearPreferences())
        .doOnComplete(() -> activeUser.set(null)); // Clear cached user on sign out
  }
}
