package com.optlab.banhangso.repositories;

import androidx.annotation.NonNull;

import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRepositoryImpl implements AuthRepository {
    private final FirebaseAuthService firebaseAuthService;
    private final PreferenceRepository preferenceRepository;
    private final UserRepository userRepository;
    private final ErrorHandler errorHandler;

    private User inMemoryUser;

    public AuthRepositoryImpl(
            FirebaseAuthService firebaseAuthService,
            PreferenceRepository preferenceRepository,
            UserRepository userRepository,
            ErrorHandler errorHandler) {
        this.firebaseAuthService = firebaseAuthService;
        this.preferenceRepository = preferenceRepository;
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
    public Single<Result<Boolean>> isLoggedIn() {
        return firebaseAuthService
                .isLoggedIn()
                .flatMap(result -> Single.just(new Result.Success<>(result)));
    }

    private Single<Result<Void>> getUserAndSetPreferences(@NonNull String userId) {
        return userRepository
                .getUser(userId)
                .toSingle()
                .flatMap(
                        userResult -> {
                            if (userResult instanceof Result.Success<User> success) {
                                User user = success.getData();
                                if (user != null) {
                                    preferenceRepository.setUser(user);
                                    return Single.just(
                                            (Result<Void>) new Result.Success<Void>(null));
                                }
                            } else if (userResult instanceof Result.Failure<User> failure) {
                                return Single.just(new Result.Failure<>(failure.getError()));
                            }
                            return null;
                        });
    }

    @Override
    public Single<Result<User>> getUser() {
        return Single.create(
                emitter -> {
                    // First check if user is in memory
                    if (inMemoryUser != null) {
                        emitter.onSuccess(new Result.Success<>(inMemoryUser));
                        return;
                    }

                    // Try to get user from preferences
                    inMemoryUser = preferenceRepository.getUser();
                    if (inMemoryUser == null) {
                        Throwable throwable =
                                new IllegalStateException("User is not set in preferences");
                        emitter.onSuccess(new Result.Failure<>(errorHandler.getError(throwable)));
                        return;
                    }

                    emitter.onSuccess(new Result.Success<>(inMemoryUser));
                });
    }

    @Override
    public Completable setUser(@NonNull User user) {
        return Completable.fromCallable(
                () -> {
                    inMemoryUser = user;
                    preferenceRepository.setUser(user);
                    return null;
                });
    }
}
