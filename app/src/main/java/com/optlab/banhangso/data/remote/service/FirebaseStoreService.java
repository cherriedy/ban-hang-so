package com.optlab.banhangso.data.remote.service;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.remote.dto.StoreDto;

import io.reactivex.rxjava3.core.Single;

import java.util.ArrayList;
import java.util.List;

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
