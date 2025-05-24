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

    /**
     * Create a new user in the Firestore database
     *
     * <p>This method takes a unique identifier (UUID) and a User object, and attempts to create a
     * new user in the Firestore database. If the operation is successful, it invokes the callback
     * with true. If it fails, it invokes the callback with false.
     *
     * @param uuid the unique identifier for the user
     * @param user the User object to be created
     * @param callback a callback to handle success or failure
     */
    @Override
    public void createUser(
            @NonNull String uuid, @NonNull User user, @NonNull Consumer<Boolean> callback) {
        firestore
                .collection(COLLECTION_PATH)
                .document(uuid)
                .set(user)
                .addOnSuccessListener(
                        aVoid -> {
                            Timber.d("User created with ID: %s", uuid);
                            callback.accept(true);
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.e("Error creating user: %s", e.getMessage());
                            callback.accept(false);
                        });
    }
}
