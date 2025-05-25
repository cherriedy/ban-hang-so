package com.optlab.banhangso.data.remote.service;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.remote.dto.UserDto;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

import timber.log.Timber;

public class FirebaseUserService {
    private static final String COLLECTION_PATH = "users";

    private final FirebaseFirestore firestore;

    public FirebaseUserService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Create a new user in the Firestore database
     *
     * <p>This method takes a unique identifier (UUID) and a UserDto object, and attempts to create
     * a new user in the Firestore database. If the operation is successful, it returns the UserDto
     * with the ID set. If it fails, it emits an error.
     *
     * @param uuid the unique identifier for the user
     * @param userDto the UserDto object to be created
     * @return Single emitting the created UserDto with ID set
     */
    public Single<UserDto> saveUser(@NonNull String uuid, @NonNull UserDto userDto) {
        return Single.create(
                emitter ->
                        firestore
                                .collection(COLLECTION_PATH)
                                .document(uuid)
                                .set(userDto)
                                .addOnSuccessListener(
                                        unused -> {
                                            userDto.setId(uuid);
                                            emitter.onSuccess(userDto);
                                        })
                                .addOnFailureListener(emitter::onError));
    }

    /**
     * Retrieve a user by their unique identifier (UUID) from the Firestore database.
     *
     * <p>This method queries the Firestore database for a user document with the specified UUID. If
     * the document exists, it converts it to a UserDto object and emits it. If the document does
     * not exist or an error occurs, it emits an error.
     *
     * @param uuid the unique identifier of the user to retrieve
     * @return Maybe emitting the UserDto if found, or completing if not found
     */
    public Maybe<UserDto> getUserById(@NonNull String uuid) {
        return Maybe.create(
                emitter -> {
                    firestore
                            .collection(COLLECTION_PATH)
                            .document(uuid)
                            .get()
                            .addOnSuccessListener(doc -> emitter.onSuccess(docToUserDto(doc)))
                            .addOnFailureListener(emitter::onError);
                });
    }

    /**
     * Convert a Firestore DocumentSnapshot to a UserDto object.
     *
     * <p>This method takes a DocumentSnapshot, checks if it is null, and if not, converts it to a
     * UserDto object. It also sets the ID of the UserDto to the document's ID.
     *
     * @param doc the DocumentSnapshot to convert
     * @return UserDto object with ID set, or null if the document is null
     */
    private UserDto docToUserDto(DocumentSnapshot doc) {
        UserDto userDto = doc.toObject(UserDto.class);
        if (doc == null) {
            Timber.e("Document is null for user ID: %s", doc.getId());
            return null;
        }
        userDto.setId(doc.getId());
        return userDto;
    }
}
