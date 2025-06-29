package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface UserRepository {
  Single<Result<User>> setUser(@NonNull User user);

  Maybe<Result<User>> getUser(@NonNull String userId);
}
