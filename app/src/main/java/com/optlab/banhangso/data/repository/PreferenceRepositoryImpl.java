package com.optlab.banhangso.data.repository;

import androidx.annotation.Nullable;

import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.repository.preferences.UserPreferenceStorage;
import com.optlab.banhangso.domain.util.SortOption;

import javax.inject.Inject;

public class PreferenceRepositoryImpl implements PreferenceRepository {
    private final UserPreferenceStorage userPreferenceStorage;

    @Inject
    public PreferenceRepositoryImpl(UserPreferenceStorage userPreferenceStorage) {
        this.userPreferenceStorage = userPreferenceStorage;
    }

    @Override
    public void setSortOption(SortOption<? extends Enum<?>> sortOption, String key) {
        userPreferenceStorage.setSortOption(sortOption, key);
    }

    @Override
    public SortOption<?> getSortOption(String key) {
        return userPreferenceStorage.getSortOption(key);
    }

    @Override
    public void clearAllPreferences() {
        userPreferenceStorage.clearAllPreferences();
    }

    @Override
    public void setSelectedStoreId(String storeId) {
        userPreferenceStorage.setSelectedStoreId(storeId);
    }

    @Override
    public String getSelectedStoreId() {
        return userPreferenceStorage.getSelectedStoreId();
    }

    @Override
    public String getSelectedStoreName() {
        return userPreferenceStorage.getSelectedStoreName();
    }

    @Override
    public void setLayoutMode(Boolean isGrid) {
        userPreferenceStorage.setLayoutMode(isGrid);
    }

    @Override
    public Boolean getLayoutMode() {
        return userPreferenceStorage.getLayoutMode();
    }

    @Override
    public void saveAuthenticatedUser(User user) {
        userPreferenceStorage.saveAuthenticatedUser(user);
    }

    @Override
    @Nullable
    public User getAuthenticatedUser() {
        return userPreferenceStorage.getAuthenticatedUser();
    }

    @Override
    public boolean isAuthenticated() {
        return userPreferenceStorage.isAuthenticated();
    }

    @Override
    @Nullable
    public String getAuthenticatedUserId() {
        return userPreferenceStorage.getAuthenticatedUserId();
    }

    @Override
    @Nullable
    public String getAuthenticatedUserEmail() {
        return userPreferenceStorage.getAuthenticatedUserEmail();
    }

    @Override
    @Nullable
    public String getAuthenticatedUserPhone() {
        return userPreferenceStorage.getAuthenticatedUserPhone();
    }

    @Override
    @Nullable
    public String getAuthenticatedContactName() {
        return userPreferenceStorage.getAuthenticatedContactName();
    }

    @Nullable
    @Override
    public String getAuthenticatedUserAvatar() {
        return userPreferenceStorage.getAuthenticatedUserAvatar();
    }

    @Override
    public void setAuthenticatedUserName(String contactName) {
        userPreferenceStorage.setAuthenticatedContactName(contactName);
    }

    @Override
    public void setAuthenticatedUserPhone(String phone) {
        userPreferenceStorage.setAuthenticatedUserPhone(phone);
    }

    @Override
    public void setAuthenticatedUserEmail(String email) {
        userPreferenceStorage.setAuthenticatedUserEmail(email);
    }

    @Override
    public void setAuthenticatedUserId(String userId) {
        userPreferenceStorage.setAuthenticatedUserId(userId);
    }

    @Override
    public void setAuthenticatedUserAvatar(String avatarUrl) {
        userPreferenceStorage.setAuthenticatedUserAvatar(avatarUrl);
    }

    @Override
    public void setAuthenticatedUser(User user) {
        userPreferenceStorage.setAuthenticatedUserId(user.getId());
        userPreferenceStorage.setAuthenticatedContactName(user.getContactName());
        userPreferenceStorage.setAuthenticatedUserEmail(user.getEmail());
        userPreferenceStorage.setAuthenticatedUserPhone(user.getPhone());
        userPreferenceStorage.setAuthenticatedUserAvatar(user.getImageUrl());
    }

    @Override
    public void setSelectedStoreName(String storeName) {
        userPreferenceStorage.setSelectedStoreName(storeName);
    }
}
