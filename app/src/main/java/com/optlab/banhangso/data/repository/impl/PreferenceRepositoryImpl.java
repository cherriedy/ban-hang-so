package com.optlab.banhangso.data.repository.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.model.app.SortOption;
import com.optlab.banhangso.data.reference.UserPreferenceManager;
import com.optlab.banhangso.data.repository.PreferenceRepository;

public class PreferenceRepositoryImpl implements PreferenceRepository {
    private final UserPreferenceManager userPreferenceManager;

    public PreferenceRepositoryImpl(@NonNull Context context) {
        this.userPreferenceManager = new UserPreferenceManager(context);
    }

    @Override
    public void setSortOption(SortOption<? extends Enum<?>> sortOption, String key) {
        userPreferenceManager.setSortOption(sortOption, key);
    }

    @Override
    public SortOption<?> getSortOption(String key) {
        return userPreferenceManager.getSortOption(key);
    }
}
