package com.optlab.banhangso.data.repository;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.model.User;

import java.util.function.Consumer;

public interface UserRepository {
    void createUser(@NonNull String uuid, @NonNull User user, Consumer<Boolean> callback);
}
