package com.optlab.banhangso.data.repository.impl;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.model.User;
import com.optlab.banhangso.data.repository.UserRepository;

import timber.log.Timber;

import java.util.function.Consumer;

public class UserRepositoryImpl implements UserRepository {
    private static final String COLLECTION_PATH = "users";

    private final FirebaseFirestore firestore;

    public UserRepositoryImpl(@NonNull FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public void createUser(@NonNull User user, @NonNull Consumer<Boolean> callback) {
        firestore
                .collection(COLLECTION_PATH)
                .add(user)
                .addOnSuccessListener(
                        docRef -> {
                            Timber.d("User created with ID: %s", docRef.getId());
                            callback.accept(true);
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.e("Error creating user: %s", e.getMessage());
                            callback.accept(false);
                        });
    }
}
