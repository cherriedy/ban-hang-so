package com.optlab.banhangso.domain.repository.preferences;

import androidx.annotation.Nullable;

import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.util.SortOption;

public interface UserPreferenceStorage {
    String KEY_PRODUCT_SORT_OPTION = "product_sort_option";
    String KEY_BRAND_SORT_OPTION = "brand_sort_option";
    String KEY_CATEGORY_SORT_OPTION = "category_sort_option";
    String KEY_PRODUCT_LAYOUT_MODE = "product_layout_mode";
    String KEY_SELECTED_STORE_ID = "selected_store_id";
    String KEY_SELECTED_STORE_NAME = "selected_store_name";
    String KEY_AUTH_USER_ID = "auth_user_id";
    String KEY_AUTH_USER_EMAIL = "auth_user_email";
    String KEY_AUTH_USER_PHONE = "auth_user_phone";
    String KEY_AUTH_CONTACT_NAME = "auth_contact_name";
    String KEY_AUTH_USER_JSON = "auth_user_json";
    String KEY_AUTH_USER_AVATAR = "auth_user_avatar";

    /**
     * Sets a sort option in the preferences.
     *
     * @param sortOption The sort option to save
     * @param key The preference key to use
     */
    void setSortOption(SortOption<? extends Enum<?>> sortOption, String key);

    /**
     * Gets a sort option from the preferences.
     *
     * @param key The preference key to retrieve
     * @return The stored sort option, or null if not found
     */
    SortOption<?> getSortOption(String key);

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
     * Sets the selected store ID.
     *
     * @param storeId The ID of the selected store
     */
    void setSelectedStoreId(String storeId);

    /**
     * Gets the selected store ID.
     *
     * @return The ID of the selected store, or null if not set
     */
    void setSelectedStoreName(String storeName);

    /**
     * Get the ID of the currently selected store.
     *
     * @return The ID of the selected store, or null if no store is selected
     */
    @Nullable
    String getSelectedStoreId();

    /**
     * Gets the name of the currently selected store.
     *
     * @return The name of the selected store, or null if not set
     */
    @Nullable
    String getSelectedStoreName();

    /**
     * Clears all user preferences. This should be called when a user logs in to prevent data
     * leakage between different user sessions.
     */
    void clearAllPreferences();

    /**
     * Saves the authenticated user information. This is separate from the staff users stored in the
     * Room database.
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
     * @return The URL of the user's avatar, or null if not set
     */
    @Nullable
    String getAuthenticatedUserAvatar();

    /**
     * Sets the contact name for the authenticated user.
     *
     * @param contactName The contact name to set
     */
    void setAuthenticatedContactName(String contactName);

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
     * @param avatarUrl The URL of the user's avatar
     */
    void setAuthenticatedUserAvatar(String avatarUrl);
}
