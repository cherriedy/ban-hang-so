package com.optlab.banhangso.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.models.remote.StoreFirebaseObject;
import com.optlab.banhangso.services.interfaces.FirebaseStoreService;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * @deprecated This class is deprecated and using {@link
 *     com.optlab.banhangso.services.interfaces.RenderStoreService} instead.
 */
@Deprecated
public class FirebaseStoreServiceImpl implements FirebaseStoreService {
    private final FirebaseFirestore firestore;

    /**
     * Constructor for FirebaseStoreService
     *
     * @param firestore The FirebaseFirestore instance for database operations
     */
    public FirebaseStoreServiceImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Retrieves all stores associated with the currently authenticated user with offline support
     * using Firestore's persistence capabilities.
     *
     * @return Single emitting a list of StoreDto objects or an error if retrieval fails
     */
    @Override
    public Single<List<StoreFirebaseObject>> getUserStores(@NonNull String userId) {
        return Single.create(
                emitter -> {
                    if (userId.isBlank()) {
                        emitter.onError(
                                new FirebaseFirestoreException(
                                        "User ID cannot be empty",
                                        FirebaseFirestoreException.Code.INVALID_ARGUMENT));
                        return;
                    }

                    FirebaseFunctions.getInstance()
                            .getHttpsCallable("get_user_stores")
                            .call(Map.of("userId", userId))
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            onGetUserStoresSuccess(emitter, task.getResult());
                                        } else {
                                            emitter.onError(task.getException());
                                        }
                                    });
                });
    }

    @Override
    public Completable deleteStore(@NonNull String storeId) {
        return Completable.create(
                emitter -> {
                    if (storeId.isBlank()) {
                        emitter.onError(
                                new FirebaseFirestoreException(
                                        "Store ID cannot be empty",
                                        FirebaseFirestoreException.Code.INVALID_ARGUMENT));
                        return;
                    }

                    firestore
                            .collection(STORES_COLLECTION)
                            .document(storeId)
                            .delete()
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            emitter.onComplete();
                                        } else {
                                            emitter.onError(task.getException());
                                        }
                                    });
                });
    }

    /**
     * Processes successful response from the get_user_stores Cloud Function
     *
     * <p>Parses the response data from Firebase Functions, converts it to a list of StoreDto
     * objects using GSON, and emits the result via the Single emitter. Handles potential parsing
     * errors and ensures the returned list is never null.
     *
     * @param emitter The SingleEmitter to emit the result or error
     * @param result The result data from the Firebase Function call
     */
    private static void onGetUserStoresSuccess(
            SingleEmitter<List<StoreFirebaseObject>> emitter, HttpsCallableResult result) {
        if (result == null || result.getData() == null) {
            emitter.onError(
                    new FirebaseFirestoreException(
                            "Invalid response from Firebase Functions",
                            FirebaseFirestoreException.Code.INTERNAL));
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> responseMap = (Map<String, Object>) result.getData();
        Timber.d("Parsing response from getUserStore(): %s", responseMap);

        try {
            Gson gson = new Gson();

            // Convert the stores data to JSON first
            String storesJson = gson.toJson(responseMap.get(STORES_COLLECTION));
            Type storeListType = new TypeToken<List<StoreFirebaseObject>>() {}.getType();

            // Parse the JSON string into a list of FirebaseStoreObject objects
            List<StoreFirebaseObject> stores = gson.fromJson(storesJson, storeListType);

            // Ensure the list is not null, initialize to empty list if it is null
            stores = stores != null ? stores : new ArrayList<>();
            Timber.d("Successfully retrieved %d stores", stores.size());

            emitter.onSuccess(stores);
        } catch (Exception e) {
            Timber.e("Error parsing stores data: %s", e.getMessage());
            emitter.onError(
                    new FirebaseFirestoreException(
                            "Failed to parse stores data: " + e.getMessage(),
                            FirebaseFirestoreException.Code.INTERNAL));
        }
    }

    /**
     * Retrieves a single store by its unique identifier from Firestore with offline support
     *
     * @param storeId The unique identifier of the store to retrieve
     * @return Single emitting the StoreDto if found or an error if retrieval fails
     */
    @Override
    public Single<StoreFirebaseObject> getStore(@NonNull String storeId) {
        return Single.create(
                emitter -> {
                    if (storeId.isBlank()) {
                        emitter.onError(
                                new FirebaseFirestoreException(
                                        "Store ID cannot be empty",
                                        FirebaseFirestoreException.Code.INVALID_ARGUMENT));
                    }

                    firestore
                            .collection(STORES_COLLECTION)
                            .document(storeId)
                            .get()
                            .addOnCompleteListener(
                                    task -> {
                                        if (task.isSuccessful()) {
                                            onGetStoreSuccess(emitter, task.getResult());
                                        } else {
                                            emitter.onError(task.getException());
                                        }
                                    });
                });
    }

    @Override
    public Single<StoreFirebaseObject> setStore(@NonNull StoreFirebaseObject storeFirebaseObject) {
        return Single.create(
                emitter ->
                        firestore
                                .collection(STORES_COLLECTION)
                                .add(storeFirebaseObject)
                                .addOnCompleteListener(
                                        task -> {
                                            if (task.isSuccessful()) {
                                                emitter.onSuccess(storeFirebaseObject);
                                            } else {
                                                emitter.onError(task.getException());
                                            }
                                        }));
    }

    private static void onGetStoreSuccess(
            SingleEmitter<StoreFirebaseObject> emitter,
            @NonNull DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            StoreFirebaseObject storeFirebaseObject = toStore(documentSnapshot);
            if (storeFirebaseObject == null) {
                emitter.onError(
                        new FirebaseFirestoreException(
                                "Failed to parse store data",
                                FirebaseFirestoreException.Code.INTERNAL));
            }
            emitter.onSuccess(storeFirebaseObject);
        } else {
            emitter.onError(
                    new FirebaseFirestoreException(
                            "Store with ID " + documentSnapshot.getId() + " does not exist",
                            FirebaseFirestoreException.Code.NOT_FOUND));
        }
    }

    @Nullable private static StoreFirebaseObject toStore(@NonNull DocumentSnapshot documentSnapshot) {
        // Convert the DocumentSnapshot to a StoreFirebaseObject
        StoreFirebaseObject storeFirebaseObject =
                documentSnapshot.toObject(StoreFirebaseObject.class);

        // If the conversion is successful, set the ID and return the object
        if (storeFirebaseObject != null) {
            storeFirebaseObject.setId(documentSnapshot.getId());
            return storeFirebaseObject;
        }

        return null;
    }
}
