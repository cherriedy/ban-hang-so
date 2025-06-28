package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {
    Single<Result<Void>> logInWithEmailAndPassword(@NonNull String email, @NonNull String password);

    Single<Result<Boolean>> isLoggedIn();

    Single<Result<User>> getUser();

    Completable setUser(@NonNull User user);
}
