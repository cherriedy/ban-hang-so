package com.optlab.banhangso.repositories;

import androidx.annotation.NonNull;

import com.optlab.banhangso.internal.utilities.errorhandler.ErrorHandler;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.remote.UserFirebaseObject;
import com.optlab.banhangso.models.remote.mapper.UserFirebaseObjectMapper;
import com.optlab.banhangso.repositories.interfaces.UserRepository;
import com.optlab.banhangso.services.interfaces.FirebaseUserService;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class UserRepositoryImpl implements UserRepository {
  private final FirebaseUserService firebaseUserService;
  private final ErrorHandler errorHandler;

  public UserRepositoryImpl(FirebaseUserService firebaseUserService, ErrorHandler errorHandler) {
    this.firebaseUserService = firebaseUserService;
    this.errorHandler = errorHandler;
  }

  @Override
  public Single<Result<User>> setUser(@NonNull User user) {
    UserFirebaseObject userFirebaseObject = UserFirebaseObjectMapper.fromDomain(user);
    return firebaseUserService
        .setUser(userFirebaseObject)
        .flatMap(object -> Single.just((Result<User>) new Result.Success<>(user)))
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }

  @Override
  public Maybe<Result<User>> getUser(@NonNull String userId) {
    return firebaseUserService
        .getUser(userId)
        .flatMap(
            userFirebaseObject -> {
              User user = UserFirebaseObjectMapper.toDomain(userFirebaseObject);
              return Maybe.just((Result<User>) new Result.Success<>(user));
            })
        .onErrorReturn(throwable -> new Result.Failure<>(errorHandler.getError(throwable)));
  }
}
