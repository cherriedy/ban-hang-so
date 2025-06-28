package com.optlab.banhangso.models.remote.mapper;

import androidx.annotation.NonNull;

import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.remote.UserFirebaseObject;

import java.util.List;
import java.util.stream.Collectors;

public class UserFirebaseObjectMapper {

    private UserFirebaseObjectMapper() {
    }

    @NonNull
    public static User toDomain(@NonNull UserFirebaseObject userFirebaseObject) {
        User user = new User();
        user.setId(userFirebaseObject.getId());
        user.setName(userFirebaseObject.getContactName());
        user.setPhone(userFirebaseObject.getPhone());
        user.setEmail(userFirebaseObject.getEmail());
        user.setImageUrl(userFirebaseObject.getImageUrl());
        user.setStores(toUserStoreDomains(userFirebaseObject.getStores()));
        user.setCreatedAt(userFirebaseObject.getCreatedAt());
        user.setUpdatedAt(userFirebaseObject.getUpdatedAt());
        return user;
    }

    @NonNull
    public static UserFirebaseObject fromDomain(@NonNull User user) {
        UserFirebaseObject userFirebaseObject = new UserFirebaseObject();
        userFirebaseObject.setId(user.getId());
        userFirebaseObject.setContactName(user.getName());
        userFirebaseObject.setPhone(user.getPhone());
        userFirebaseObject.setEmail(user.getEmail());
        userFirebaseObject.setImageUrl(user.getImageUrl());
        userFirebaseObject.setStores(fromUserStoreDomains(user.getStores()));
        userFirebaseObject.setCreatedAt(user.getCreatedAt());
        userFirebaseObject.setUpdatedAt(user.getUpdatedAt());
        return userFirebaseObject;
    }

    @NonNull
    private static User.Store toUserStoreDomain(
            @NonNull UserFirebaseObject.Store firebaseUserStore) {
        User.Store store = new User.Store();
        store.setId(firebaseUserStore.getId());
        store.setRole(firebaseUserStore.getRole());
        return store;
    }

    private static List<User.Store> toUserStoreDomains(
            @NonNull List<UserFirebaseObject.Store> firebaseUserStoresStores) {
        return firebaseUserStoresStores.stream()
                .map(UserFirebaseObjectMapper::toUserStoreDomain)
                .collect(Collectors.toList());
    }

    @NonNull
    private static UserFirebaseObject.Store fromUserStoreDomain(@NonNull User.Store userStore) {
        UserFirebaseObject.Store firebaseUserStore = new UserFirebaseObject.Store();
        firebaseUserStore.setId(userStore.getId());
        firebaseUserStore.setRole(userStore.getRole());
        return firebaseUserStore;
    }

    private static List<UserFirebaseObject.Store> fromUserStoreDomains(
            @NonNull List<User.Store> userStores) {
        return userStores.stream()
                .map(UserFirebaseObjectMapper::fromUserStoreDomain)
                .collect(Collectors.toList());
    }
}
