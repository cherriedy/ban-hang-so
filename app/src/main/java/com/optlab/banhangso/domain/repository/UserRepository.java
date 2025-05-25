package com.optlab.banhangso.domain.repository;

import androidx.annotation.NonNull;

import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.util.Resource;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
    Flowable<Resource<User>> saveUser(@NonNull String uuid, @NonNull User domainUser);

    Single<Resource<User>> saveUserRemote(@NonNull String uuid, @NonNull User domainUser);

    Maybe<Resource<User>> getUserById(@NonNull String uuid);
}
