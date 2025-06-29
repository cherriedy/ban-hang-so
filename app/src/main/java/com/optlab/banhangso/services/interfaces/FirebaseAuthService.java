package com.optlab.banhangso.services.interfaces;

import androidx.annotation.NonNull;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

public interface FirebaseAuthService {
  Single<String> logInWithEmailAndPassword(@NonNull String email, @NonNull String password);

  Observable<Boolean> isAuthenticated();

  Completable signOut();
}
