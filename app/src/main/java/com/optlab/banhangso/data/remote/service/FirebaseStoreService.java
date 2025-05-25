package com.optlab.banhangso.data.remote.service;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.optlab.banhangso.data.remote.dto.StoreDto;

import io.reactivex.rxjava3.core.Single;

import timber.log.Timber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseStoreService {
    private final FirebaseFirestore firestore;
    private static final String STORE_COLLECTION = "stores";

    public FirebaseStoreService(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    /** Get all stores from Firebase */
    public Single<List<StoreDto>> getAllStores() {
        return Single.create(
                emitter -> {
                    firestore
                            .collection(STORE_COLLECTION)
                            .get()
                            .addOnSuccessListener(
                                    snapshots -> {
                                        List<StoreDto> stores = new ArrayList<>();
                                        for (DocumentSnapshot document : snapshots.getDocuments()) {
                                            StoreDto store = document.toObject(StoreDto.class);
                                            if (store != null) {
                                                store.setId(document.getId());
                                                stores.add(store);
                                            }
                                        }
                                        emitter.onSuccess(stores);
                                    })
                            .addOnFailureListener(emitter::onError);
                });
    }

    /**
     * @noinspection unchecked
     */
    public Single<List<StoreDto>> getAllStoresByUserId(String userId) {
        return Single.create(
                emitter -> {
                    // Enhanced logging - confirm the actual userId value
                    Timber.d("getAllStoresByUserId called with userId: %s", userId);

                    // Check for null or empty userId early
                    if (userId == null || userId.isEmpty()) {
                        Timber.e("getAllStoresByUserId failed: userId is null or empty");
                        emitter.onError(new Exception("User ID is null or empty"));
                        return;
                    }

                    // Create data to pass to the function - using HashMap
                    Map<String, Object> data = new HashMap<>();
                    data.put("userId", userId);

                    FirebaseFunctions.getInstance()
                            .getHttpsCallable("get_user_stores")
                            .call(data)
                            .addOnSuccessListener(result -> {
                                if (result != null && result.getData() instanceof List) {
                                    List<Map<String, Object>> resultData =
                                            (List<Map<String, Object>>) result.getData();
                                    List<StoreDto> stores = new ArrayList<>();
                                    for (Map<String, Object> item : resultData) {
                                        try {
                                            StoreDto store = new StoreDto();
                                            // Populate StoreDto fields from the map
                                            store.setId((String) item.get("id"));
                                            store.setName((String) item.get("name"));
                                            store.setDescription((String) item.get("description"));
                                            stores.add(store);
                                        } catch (Exception e) {
                                            Timber.e(e, "Error parsing store data: %s", item);
                                        }
                                    }
                                    Timber.d("Successfully retrieved %d stores", stores.size());
                                    emitter.onSuccess(stores);
                                } else {
                                    Timber.e("getAllStoresByUserId failed: Invalid data format");
                                    emitter.onError(new Exception("Invalid data format"));
                                }
                            })
                            .addOnFailureListener(exception -> {
                                Timber.e(exception, "getAllStoresByUserId failed: %s", exception.getMessage());
                                emitter.onError(exception);
                            });
                });
    }

    /** Get a specific store by ID */
    public Single<StoreDto> getStoreById(String storeId) {
        return Single.create(
                emitter -> {
                    firestore
                            .collection(STORE_COLLECTION)
                            .document(storeId)
                            .get()
                            .addOnSuccessListener(
                                    document -> {
                                        if (document.exists()) {
                                            StoreDto store = document.toObject(StoreDto.class);
                                            if (store != null) {
                                                store.setId(document.getId());
                                                emitter.onSuccess(store);
                                            } else {
                                                emitter.onError(
                                                        new Exception(
                                                                "Failed to parse store data"));
                                            }
                                        } else {
                                            emitter.onError(new Exception("Store not found"));
                                        }
                                    })
                            .addOnFailureListener(emitter::onError);
                });
    }

    /** Save a store to Firebase */
    public Single<StoreDto> saveStore(StoreDto storeDto) {
        return Single.create(
                emitter -> {
                    // Determine if we need to create or update
                    if (storeDto.getId() == null || storeDto.getId().isEmpty()) {
                        // Create new document with auto-generated ID
                        firestore
                                .collection(STORE_COLLECTION)
                                .add(storeDto)
                                .addOnSuccessListener(
                                        docRefs -> {
                                            storeDto.setId(docRefs.getId());
                                            emitter.onSuccess(storeDto);
                                        })
                                .addOnFailureListener(emitter::onError);
                    } else {
                        // Update existing document by ID
                        firestore
                                .collection(STORE_COLLECTION)
                                .document(storeDto.getId())
                                .set(storeDto)
                                .addOnSuccessListener(aVoid -> emitter.onSuccess(storeDto))
                                .addOnFailureListener(emitter::onError);
                    }
                });
    }

    /** Delete a store from Firebase */
    public Single<Boolean> deleteStore(String storeId) {
        return Single.create(
                emitter -> {
                    firestore
                            .collection(STORE_COLLECTION)
                            .document(storeId)
                            .delete()
                            .addOnSuccessListener(aVoid -> emitter.onSuccess(true))
                            .addOnFailureListener(emitter::onError);
                });
    }
}
