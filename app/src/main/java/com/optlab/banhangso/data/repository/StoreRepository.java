package com.optlab.banhangso.data.repository;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.model.Store;
import com.optlab.banhangso.data.model.app.Resource;

import io.reactivex.rxjava3.core.Flowable;

import java.util.List;

/**
 * Interface for Store Repository operations Provides methods to interact with store data from both
 * local database and remote sources
 */
public interface StoreRepository {

    /**
     * Get all stores with network bound resource This method will load data from local database and
     * refresh from network if necessary
     *
     * @return Flowable emitting a Resource wrapping a list of Store domain models
     */
    @NonNull
    Flowable<Resource<List<Store>>> getAllStores();

    /**
     * Get a specific store by ID with network bound resource This method will load data from local
     * database and refresh from network if necessary
     *
     * @param storeId The unique identifier of the store to retrieve
     * @return Flowable emitting a Resource wrapping a Store domain model
     */
    @NonNull
    Flowable<Resource<Store>> getStoreById(@NonNull String storeId);

    /**
     * Save a store to both local database and Firebase
     *
     * @param domainStore The Store domain model to save
     * @return Flowable emitting a Resource wrapping the saved Store domain model
     */
    @NonNull
    Flowable<Resource<Store>> saveStore(@NonNull Store domainStore);

    /**
     * Delete a store from both local database and Firebase
     *
     * @param storeId The unique identifier of the store to delete
     * @return Flowable emitting a Resource wrapping a Boolean indicating success/failure
     */
    @NonNull
    Flowable<Resource<Boolean>> deleteStore(@NonNull String storeId);
}
