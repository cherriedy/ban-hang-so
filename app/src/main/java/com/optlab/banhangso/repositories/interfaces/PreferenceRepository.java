package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;

public interface PreferenceRepository {
    /** Set a sort option for a specific key. */
    void setSortOption(SortOption<? extends Enum<?>> sortOption, String key);

    /** Get the sort option associated with a specific key. */
    SortOption<?> getSortOption(String key);

    /** Clear all user preferences. */
    void clearPreferences();

    /**
     * Sets the layout mode for products.
     *
     * @param isGrid True for grid layout, false for list layout
     */
    void setLayoutMode(Boolean isGrid);

    /**
     * Gets the layout mode for products.
     *
     * @return True for grid layout, false for list layout
     */
    Boolean getLayoutMode();

    void setUser(@NonNull User user);

    @Nullable User getUser();

    void setStore(@NonNull RoleStore roleStore);

    @Nullable RoleStore getStore();
}
