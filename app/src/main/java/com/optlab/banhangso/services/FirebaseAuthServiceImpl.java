package com.optlab.banhangso.services;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import timber.log.Timber;

public class FirebaseAuthServiceImpl implements FirebaseAuthService {

    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthServiceImpl(FirebaseAuth firebaseAuth) {
        this.firebaseAuth = firebaseAuth;
    }

    @Override
    public Single<String> logInWithEmailAndPassword(
            @NonNull String email, @NonNull String password) {
        return Single.create(
                emitter ->
                        firebaseAuth
                                .signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(
                                        task ->
                                                onLogInWithEmailAndPasswordComplete(
                                                        emitter, task)));
    }

    @Override
    public Single<Boolean> isLoggedIn() {
        return Single.fromCallable(() -> firebaseAuth.getCurrentUser() != null);
    }

    private void onLogInWithEmailAndPasswordComplete(
            SingleEmitter<String> emitter, @NonNull Task<AuthResult> task) {
        Timber.d("onLogInWithEmailAndPasswordComplete: %s", task.isSuccessful());
        if (task.isSuccessful()) {
            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            if (firebaseUser == null) {
                emitter.onError(
                        new FirebaseAuthException(
                                "ERROR_NO_CURRENT_USER",
                                "No current user found after successful login."));
                return;
            }
            emitter.onSuccess(firebaseUser.getUid());
        } else {
            emitter.onError(task.getException());
        }
    }
}
