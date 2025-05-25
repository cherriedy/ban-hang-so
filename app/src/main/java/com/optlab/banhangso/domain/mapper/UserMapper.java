package com.optlab.banhangso.domain.mapper;

import com.optlab.banhangso.data.local.entity.UserEntity;
import com.optlab.banhangso.data.remote.dto.UserDto;
import com.optlab.banhangso.domain.model.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * UserMapper provides methods to transform between different user model representations: - User:
 * the domain model (clean POJO) - UserDto: the data transfer object with Firebase and data binding
 * annotations - UserEntity: the database entity
 */
public class UserMapper {

    /** Converts a UserDto to a domain User model. */
    public static User dtoToDomain(UserDto dto) {
        if (dto == null) {
            return null;
        }

        List<User.Store> stores = null;
        if (dto.getStores() != null) {
            stores =
                    dto.getStores().stream()
                            .map(storeDto -> new User.Store(storeDto.getId(), storeDto.getRole()))
                            .collect(Collectors.toList());
        }

        return new User.Builder()
                .setId(dto.getId())
                .setContactName(dto.getContactName())
                .setEmail(dto.getEmail())
                .setPhone(dto.getPhone())
                .setImageUrl(dto.getImageUrl())
                .setStores(stores)
                .setCreatedAt(dto.getCreatedAt())
                .setUpdatedAt(dto.getUpdatedAt())
                .build();
    }

    /** Converts a domain User model to a UserDto. */
    public static UserDto domainToDto(User user) {
        if (user == null) {
            return null;
        }

        List<UserDto.Store> stores = null;
        if (user.getStores() != null) {
            stores =
                    user.getStores().stream()
                            .map(store -> new UserDto.Store(store.getId(), store.getRole()))
                            .collect(Collectors.toList());
        }

        UserDto dto =
                new UserDto.Builder()
                        .setContactName(user.getContactName())
                        .setEmail(user.getEmail())
                        .setPhone(user.getPhone())
                        .setImageUrl(user.getImageUrl())
                        .setStores(stores)
                        .build();

        dto.setId(user.getId());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());

        return dto;
    }

    /** Converts a UserEntity to a domain User model. */
    public static User entityToDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        List<User.Store> stores = null;
        stores =
                entity.getStores().stream()
                        .map(
                                storeEntity ->
                                        new User.Store(storeEntity.getId(), storeEntity.getRole()))
                        .collect(Collectors.toList());

        return new User.Builder()
                .setId(entity.getId())
                .setContactName(entity.getContactName())
                .setEmail(entity.getEmail())
                .setPhone(entity.getPhone())
                .setImageUrl(entity.getImageUrl())
                .setStores(stores)
                .setCreatedAt(entity.getCreatedAt())
                .setUpdatedAt(entity.getUpdatedAt())
                .build();
    }

    /** Converts a domain User model to a UserEntity. */
    public static UserEntity domainToEntity(User user) {
        if (user == null) {
            return null;
        }

        UserEntity entity = new UserEntity();
        entity.setId(user.getId());
        entity.setContactName(user.getContactName());
        entity.setEmail(user.getEmail());
        entity.setPhone(user.getPhone());
        entity.setImageUrl(user.getImageUrl());
        entity.setStores(convertStoreListForEntity(user.getStores()));
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());

        return entity;
    }

    public static UserEntity dtoToEntity(UserDto dto) {
        User user = dtoToDomain(dto);
        return domainToEntity(user);
    }

    /** Handles conversion of Store lists for entity storage */
    private static List<User.Store> convertStoreListForEntity(List<User.Store> stores) {
        // This method assumes that the User.Store can be directly used in UserEntity
        // If changes are needed in the future, they can be handled here
        return stores;
    }
}
