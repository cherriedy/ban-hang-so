package com.optlab.banhangso.data.local.dao;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.optlab.banhangso.data.local.entity.UserEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM UserEntity")
    Flowable<List<UserEntity>> getAllUsers();

    @Query("SELECT * FROM UserEntity WHERE id = :userId")
    Single<UserEntity> getUserById(@NonNull String userId);

    @Query("SELECT * FROM UserEntity WHERE email = :email")
    Maybe<UserEntity> getUserByEmail(@NonNull String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(@NonNull UserEntity user);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Single<Long> insertUserAndGetId(@NonNull UserEntity user);

    @Update
    Single<Integer> updateUser(@NonNull UserEntity user);

    @Delete
    Single<Integer> deleteUser(@NonNull UserEntity user);
}
