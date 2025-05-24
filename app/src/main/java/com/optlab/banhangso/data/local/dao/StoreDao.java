package com.optlab.banhangso.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.optlab.banhangso.data.local.entity.StoreEntity;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface StoreDao {
    /** Get all stores as a Flowable to observe database changes */
    @NotNull
    @Query("SELECT * FROM StoreEntity")
    Flowable<List<StoreEntity>> getAllStores();

    /** Get a single store by ID */
    @Query("SELECT * FROM StoreEntity WHERE id = :storeId")
    Single<StoreEntity> getStoreById(@NotNull String storeId);

    /** Get a store that may not exist */
    @Query("SELECT * FROM StoreEntity WHERE name = :name")
    Maybe<StoreEntity> getStoreByName(@NotNull String name);

    /** Insert a store without needing the result */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertStore(@NotNull StoreEntity store);

    /** Insert multiple stores at once */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertStores(@NotNull List<StoreEntity> stores);

    /** Update a store and get number of rows affected */
    @Update
    Single<Integer> updateStore(@NotNull StoreEntity store);

    /** Delete a store and get number of rows affected */
    @Delete
    Single<Integer> deleteStore(@NotNull StoreEntity store);

    /** Delete a store by ID */
    @Query("DELETE FROM StoreEntity WHERE id = :storeId")
    Completable deleteStoreById(@NotNull String storeId);
}
