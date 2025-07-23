package com.optlab.banhangso.services;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import timber.log.Timber;

public class FirebaseAuthServiceImpl implements FirebaseAuthService {

  private final FirebaseAuth firebaseAuth;

  public FirebaseAuthServiceImpl(FirebaseAuth firebaseAuth) {
    this.firebaseAuth = firebaseAuth;
  }

  @Override
  public Single<String> logInWithEmailAndPassword(@NonNull String email, @NonNull String password) {
    return Single.create(
        emitter ->
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> onLogInWithEmailAndPasswordComplete(emitter, task)));
  }

  @Override
  public Observable<Boolean> isAuthenticatedObservable() {
    return Observable.create(
        emitter -> {
          FirebaseAuth.AuthStateListener authStateListener =
              firebaseAuth -> emitter.onNext(firebaseAuth.getCurrentUser() != null);

          // Set the listener to the FirebaseAuth instance.
          firebaseAuth.addAuthStateListener(authStateListener);

          // Set cancellable to remove the listener when the observable is disposed.
          emitter.setCancellable(() -> firebaseAuth.removeAuthStateListener(authStateListener));
        });
  }

  @Override
  public boolean isAuthenticated() {
    return firebaseAuth.getCurrentUser() != null;
  }

  @Override
  public Completable signOut() {
    return Completable.fromAction(firebaseAuth::signOut);
  }

  private void onLogInWithEmailAndPasswordComplete(
      SingleEmitter<String> emitter, @NonNull Task<AuthResult> task) {
    Timber.d("onLogInWithEmailAndPasswordComplete: %s", task.isSuccessful());
    if (task.isSuccessful()) {
      FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
      if (firebaseUser == null) {
        emitter.onError(
            new FirebaseAuthException(
                "ERROR_NO_CURRENT_USER", "No current user found after successful login."));
        return;
      }
      emitter.onSuccess(firebaseUser.getUid());
    } else {
      emitter.onError(task.getException());
    }
  }
}
