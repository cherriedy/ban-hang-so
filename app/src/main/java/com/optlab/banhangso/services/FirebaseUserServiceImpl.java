package com.optlab.banhangso.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.optlab.banhangso.models.remote.UserFirebaseObject;
import com.optlab.banhangso.services.interfaces.FirebaseUserService;
import io.reactivex.rxjava3.core.Single;
import timber.log.Timber;

@Deprecated
public class FirebaseUserServiceImpl implements FirebaseUserService {

  private final FirebaseFirestore firestore;

  public FirebaseUserServiceImpl(FirebaseFirestore firestore) {
    this.firestore = firestore;
  }

  public Single<UserFirebaseObject> setUser(@NonNull UserFirebaseObject userFirebaseObject) {
    return Single.create(
        emitter -> {
          if (userFirebaseObject.getId() == null || userFirebaseObject.getId().isEmpty()) {
            Timber.e("User ID is null or empty");
            emitter.onError(
                new FirebaseFirestoreException(
                    "User ID is null or empty", FirebaseFirestoreException.Code.INVALID_ARGUMENT));
            return;
          }

          firestore
              .collection(USERS_COLLECTION)
              .document(userFirebaseObject.getId())
              .set(userFirebaseObject)
              .addOnCompleteListener(
                  tasks -> {
                    if (tasks.isSuccessful()) {
                      Timber.d("User created successfully with ID: %s", userFirebaseObject.getId());

                      emitter.onSuccess(userFirebaseObject);
                    } else {
                      Timber.e(
                          tasks.getException(),
                          "Failed to create user with ID: %s",
                          userFirebaseObject.getId());

                      emitter.onError(tasks.getException());
                    }
                  });
        });
  }

  public Single<UserFirebaseObject> getUser(@NonNull String userId) {
    return Single.create(
        emitter -> {
          if (userId.isBlank()) {
            emitter.onError(
                new FirebaseFirestoreException(
                    "User ID cannot be empty", FirebaseFirestoreException.Code.INVALID_ARGUMENT));
            return;
          }

          firestore
              .collection(USERS_COLLECTION)
              .document(userId)
              .get()
              .addOnCompleteListener(
                  task -> {
                    if (task.isSuccessful()) {
                      onGetUserSuccess(emitter, task, userId);
                    } else {
                      emitter.onError(task.getException());
                    }
                  });
        });
  }

  private void onGetUserSuccess(
      @NonNull io.reactivex.rxjava3.core.SingleEmitter<UserFirebaseObject> emitter,
      @NonNull Task<DocumentSnapshot> task,
      @NonNull String userId) {
    DocumentSnapshot result = task.getResult();

    if (result == null) {
      Timber.e("Document snapshot is null for user ID: %s", userId);
      emitter.onError(
          new FirebaseFirestoreException(
              "Document snapshot is null", FirebaseFirestoreException.Code.INTERNAL));
      return;
    }

    if (!result.exists()) {
      Timber.e("User not found with ID: %s", userId);
      emitter.onError(
          new FirebaseFirestoreException(
              "User not found with ID: " + userId, FirebaseFirestoreException.Code.NOT_FOUND));
      return;
    }

    UserFirebaseObject userFirebaseObject = toUser(result);
    if (userFirebaseObject != null) {
      Timber.d("Successfully retrieved user with ID: %s", userId);
      emitter.onSuccess(userFirebaseObject);
    } else {
      Timber.e("Failed to parse user data for ID: %s", userId);
      emitter.onError(
          new FirebaseFirestoreException(
              "Failed to parse user data", FirebaseFirestoreException.Code.INTERNAL));
    }
  }

  @Nullable private UserFirebaseObject toUser(@NonNull DocumentSnapshot documentSnapshot) {
    UserFirebaseObject userFirebaseObject = documentSnapshot.toObject(UserFirebaseObject.class);
    if (userFirebaseObject != null) {
      userFirebaseObject.setId(documentSnapshot.getId());
      return userFirebaseObject;
    }
    return null;
  }
}
