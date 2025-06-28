package com.optlab.banhangso.repositories;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;

import javax.inject.Inject;

public class PreferenceRepositoryImpl implements PreferenceRepository {
    private final AppPreferences appPreferences;

    @Inject
    public PreferenceRepositoryImpl(AppPreferences appPreferences) {
        this.appPreferences = appPreferences;
    }

    @Override
    public void setSortOption(SortOption<? extends Enum<?>> sortOption, String key) {
        appPreferences.setSortOption(sortOption, key);
    }

    @Override
    public SortOption<?> getSortOption(String key) {
        return appPreferences.getSortOption(key);
    }

    @Override
    public void clearPreferences() {
        appPreferences.clearPreferences();
    }

    @Override
    public void setLayoutMode(Boolean isGrid) {
        appPreferences.setLayoutMode(isGrid);
    }

    @Override
    public Boolean getLayoutMode() {
        return appPreferences.getLayoutMode();
    }

    @Override
    public void setUser(@NonNull User user) {
        appPreferences.setUser(user);
    }

    @Nullable @Override
    public User getUser() {
        return appPreferences.getUser();
    }

    @Override
    public void setStore(@NonNull RoleStore roleStore) {
        appPreferences.setStore(roleStore);
    }

    @Nullable @Override
    public RoleStore getStore() {
        return appPreferences.getStore();
    }
}
