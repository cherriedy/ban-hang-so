package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.remote.requestes.SignUpRequest;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface AuthRepository {
  Single<Result<Void>> logInWithEmailAndPassword(@NonNull String email, @NonNull String password);

  Single<Result<Void>> signUpWithEmailAndPassword(@NonNull SignUpRequest signUpRequest);

  Observable<Boolean> isAuthenticated();

  Single<Result<User>> getUser();

  Completable setUser(@NonNull User user);

  Completable signOut();
}
