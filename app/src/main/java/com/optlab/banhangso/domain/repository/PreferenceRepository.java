package com.optlab.banhangso.domain.repository;

import androidx.annotation.Nullable;

import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.util.SortOption;

public interface PreferenceRepository {
    /** Set a sort option for a specific key. */
    void setSortOption(SortOption<? extends Enum<?>> sortOption, String key);

    /** Get the sort option associated with a specific key. */
    SortOption<?> getSortOption(String key);

    /** Clear all user preferences. */
    void clearAllPreferences();

    /** Set the selected store ID for the user. */
    void setSelectedStoreId(String storeId);

    /**
     * Get the selected store ID for the user.
     *
     * @return The selected store ID, or null if not set.
     */
    String getSelectedStoreId();

    /**
     * Get the name of the selected store.
     *
     * @return The name of the selected store, or null if not set.
     */
    String getSelectedStoreName();

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

    /**
     * Saves the authenticated user information.
     *
     * @param user The authenticated user to save
     */
    void saveAuthenticatedUser(User user);

    /**
     * Gets the currently authenticated user.
     *
     * @return The authenticated user or null if not logged in
     */
    @Nullable
    User getAuthenticatedUser();

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a user is logged in, false otherwise
     */
    boolean isAuthenticated();

    /**
     * Gets the ID of the authenticated user.
     *
     * @return The ID of the authenticated user, or null if not logged in
     */
    @Nullable
    String getAuthenticatedUserId();

    /**
     * Gets the email of the authenticated user.
     *
     * @return The email of the authenticated user, or null if not logged in
     */
    @Nullable
    String getAuthenticatedUserEmail();

    /**
     * Gets the phone number of the authenticated user.
     *
     * @return The phone number of the authenticated user, or null if not logged in
     */
    @Nullable
    String getAuthenticatedUserPhone();

    /**
     * Gets the contact name of the authenticated user.
     *
     * @return The contact name of the authenticated user, or null if not set
     */
    @Nullable
    String getAuthenticatedContactName();

    /**
     * Gets the avatar URL of the authenticated user.
     *
     * @return The avatar URL of the authenticated user, or null if not set
     */
    @Nullable
    String getAuthenticatedUserAvatar();

    /**
     * Sets the contact name for the authenticated user.
     *
     * @param contactName The contact name to set
     */
    void setAuthenticatedUserName(String contactName);

    /**
     * Sets the phone number for the authenticated user.
     *
     * @param phone The phone number to set
     */
    void setAuthenticatedUserPhone(String phone);

    /**
     * Sets the email for the authenticated user.
     *
     * @param email The email to set
     */
    void setAuthenticatedUserEmail(String email);

    /**
     * Sets the ID for the authenticated user.
     *
     * @param userId The ID to set
     */
    void setAuthenticatedUserId(String userId);

    /**
     * Sets the avatar URL for the authenticated user.
     *
     * @param avatarUrl The URL of the user's avatar image.
     */
    void setAuthenticatedUserAvatar(String avatarUrl);

    /**
     * Sets the authenticated user object.
     *
     * @param user The user object to set as authenticated
     */
    void setAuthenticatedUser(User user);

    /**
     * Sets the name of the selected store.
     *
     * @param storeName The name of the selected store.
     */
    void setSelectedStoreName(String storeName);
}
