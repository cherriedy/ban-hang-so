package com.optlab.banhangso.data.repository.references;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.domain.model.Brand;
import com.optlab.banhangso.domain.model.Category;
import com.optlab.banhangso.domain.model.Product;
import com.optlab.banhangso.domain.model.User;
import com.optlab.banhangso.domain.repository.preferences.UserPreferenceStorage;
import com.optlab.banhangso.domain.util.SortOption;

import timber.log.Timber;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserPreferenceManager implements UserPreferenceStorage {
    private static final String PREFS_NAME = "user_preferences";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    /** Cached authenticated user to avoid repeated SharedPreferences access. */
    private User currentAuthenticatedUser = null;

    /**
     * Constructor for UserPreferenceManager.
     *
     * <p>Initializes SharedPreferences with the application context and sets up default values for
     * user preferences.
     *
     * @param context Application context used to access SharedPreferences.
     */
    @Inject
    public UserPreferenceManager(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        initDefaultValues();
    }

    /**
     * Initializes default sort options for products, brands, and categories if they don't exist.
     *
     * <p>This method ensures that users have default sorting preferences set from the first launch,
     * with all entities initially sorted by name in ascending order.
     */
    private void initDefaultValues() {
        if (!sharedPreferences.contains(KEY_PRODUCT_SORT_OPTION)) {
            setSortOption(new SortOption<>(Product.SortField.NAME, true), KEY_PRODUCT_SORT_OPTION);
        }
        if (!sharedPreferences.contains(KEY_BRAND_SORT_OPTION)) {
            setSortOption(new SortOption<>(Brand.SortField.NAME, true), KEY_BRAND_SORT_OPTION);
        }
        if (!sharedPreferences.contains(KEY_CATEGORY_SORT_OPTION)) {
            setSortOption(
                    new SortOption<>(Category.SortField.NAME, true), KEY_CATEGORY_SORT_OPTION);
        }
    }

    /**
     * Sets the sorting option for a specific entity type (products, brands, or categories).
     *
     * <p>Converts the sort option object to JSON and saves it in SharedPreferences using the
     * provided key to identify the entity type.
     *
     * @param sortOption The sort option to be saved.
     * @param key The key identifying which entity's sort option is being set.
     */
    @Override
    public void setSortOption(SortOption<? extends Enum<?>> sortOption, String key) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the sort option object to json.
        String sortOptionJson = gson.toJson(sortOption);

        // Save the sort option json to preference.
        editor.putString(key, sortOptionJson);

        editor.apply();
    }

    /**
     * Get the sort option from preference.
     *
     * <p>The sort option is saved as a json string in the preference. The type of the sort option
     * is determined by the key passed to this method. The key is used to identify the sort option
     * for products, brands, or categories.
     *
     * @param key The key to identify the sort option.
     * @noinspection DuplicateBranchesInSwitch
     */
    @Override
    public SortOption<?> getSortOption(String key) {
        // Get sort option json from preference.
        String sortOptionJson = sharedPreferences.getString(key, null);
        // Check if the sort option json is null.
        if (sortOptionJson == null) {
            Timber.d("Sort option json is null");
            return null;
        }

        // Get the type of sort option to prevent unchecked conversion.
        Type type =
                switch (key) {
                    case KEY_PRODUCT_SORT_OPTION ->
                            new TypeToken<SortOption<Product.SortField>>() {}.getType();
                    case KEY_BRAND_SORT_OPTION ->
                            new TypeToken<SortOption<Brand.SortField>>() {}.getType();
                    case KEY_CATEGORY_SORT_OPTION ->
                            new TypeToken<SortOption<Category.SortField>>() {}.getType();
                    default -> throw new IllegalArgumentException("Invalid key: " + key);
                };

        // Convert the sort option json to object.
        return gson.fromJson(sortOptionJson, type);
    }

    /**
     * Sets the product layout mode preference.
     *
     * <p>Determines whether products are displayed in a grid layout (true) or a list layout
     * (false). If null is passed, defaults to list view (false).
     *
     * @param isGrid Boolean indicating if grid layout should be used (true) or list layout (false).
     */
    @Override
    public void setLayoutMode(Boolean isGrid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isGrid == null) isGrid = false;
        editor.putBoolean(KEY_PRODUCT_LAYOUT_MODE, isGrid);
        editor.apply();
    }

    /**
     * Retrieves the current product layout mode preference.
     *
     * <p>Returns a boolean indicating whether products should be displayed in grid layout (true) or
     * list layout (false). Defaults to list layout (false) if not previously set.
     *
     * @return Boolean indicating current layout mode preference.
     */
    @Override
    public Boolean getLayoutMode() {
        return sharedPreferences.getBoolean(KEY_PRODUCT_LAYOUT_MODE, false);
    }

    /**
     * Saves the ID of the currently selected store.
     *
     * <p>Stores the provided store ID in SharedPreferences for later retrieval, allowing the app to
     * remember the user's selected store across sessions.
     *
     * @param storeId The unique identifier of the selected store.
     */
    @Override
    public void setSelectedStoreId(String storeId) {
        sharedPreferences.edit().putString(KEY_SELECTED_STORE_ID, storeId).apply();
    }

    /**
     * Saves the name of the currently selected store.
     * @param storeName The name of the selected store.
     */
    @Override
    public void setSelectedStoreName(String storeName) {
        sharedPreferences.edit().putString(KEY_SELECTED_STORE_NAME, storeName).apply();
    }

    /**
     * Retrieves the ID of the currently selected store.
     *
     * <p>Gets the stored store ID from SharedPreferences, allowing the app to maintain store
     * selection across app sessions. Returns an empty string if no store has been selected.
     *
     * @return The unique identifier of the selected store, or an empty string if none is selected.
     */
    @Override
    public String getSelectedStoreId() {
        return sharedPreferences.getString(KEY_SELECTED_STORE_ID, "");
    }

    /**
     * Retrieves the name of the currently selected store.
     *
     * @return The name of the selected store, or null if none is selected.
     */
    @Nullable
    @Override
    public String getSelectedStoreName() {
        return sharedPreferences.getString(KEY_SELECTED_STORE_NAME, null);
    }

    /**
     * Saves the authenticated user information. This is separate from the staff users stored in the
     * Room database.
     *
     * @param user The authenticated user to save
     */
    @Override
    public void saveAuthenticatedUser(@NonNull User user) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Save individual fields for quick access
        editor.putString(KEY_AUTH_USER_ID, user.getId());
        editor.putString(KEY_AUTH_USER_EMAIL, user.getEmail());

        // Also save the complete user object as JSON
        String userJson = gson.toJson(user);
        editor.putString(KEY_AUTH_USER_JSON, userJson);

        editor.apply();

        // Update the cache
        this.currentAuthenticatedUser = user;

        Timber.d("Saved authenticated user: %s", user.getEmail());
    }

    /**
     * Gets the currently authenticated user.
     *
     * @return The authenticated user or null if not logged in
     */
    @Nullable
    @Override
    public User getAuthenticatedUser() {
        if (currentAuthenticatedUser == null) {
            String userJson = sharedPreferences.getString(KEY_AUTH_USER_JSON, null);
            if (userJson != null) {
                try {
                    currentAuthenticatedUser = gson.fromJson(userJson, User.class);
                    Timber.d("Loaded authenticated user: %s", currentAuthenticatedUser.getEmail());
                } catch (Exception e) {
                    Timber.e(e, "Failed to parse authenticated user from JSON");
                }
            }
        }
        return currentAuthenticatedUser;
    }

    /**
     * Checks if a user is currently authenticated.
     *
     * @return true if a user is logged in, false otherwise
     */
    @Override
    public boolean isAuthenticated() {
        return sharedPreferences.contains(KEY_AUTH_USER_ID);
    }

    /**
     * Gets the ID of the currently authenticated user.
     *
     * @return The ID of the authenticated user, or null if not logged in
     */
    @Nullable
    @Override
    public String getAuthenticatedUserId() {
        String userId = sharedPreferences.getString(KEY_AUTH_USER_ID, null);
        if (userId == null) {
            Timber.w("No authenticated user ID found");
        } else {
            Timber.d("Authenticated user ID: %s", userId);
        }
        return userId;
    }

    /**
     * Gets the email of the currently authenticated user.
     *
     * @return The email of the authenticated user, or null if not logged in
     */
    @Nullable
    @Override
    public String getAuthenticatedUserEmail() {
        String email = sharedPreferences.getString(KEY_AUTH_USER_EMAIL, null);
        if (email == null) {
            Timber.w("No authenticated user email found");
        } else {
            Timber.d("Authenticated user email: %s", email);
        }
        return email;
    }

    /**
     * Gets the phone number of the currently authenticated user.
     *
     * @return The phone number of the authenticated user, or null if not logged in
     */
    @Nullable
    @Override
    public String getAuthenticatedUserPhone() {
        String phone = sharedPreferences.getString(KEY_AUTH_USER_PHONE, null);
        if (phone == null) {
            Timber.w("No authenticated user phone found");
        } else {
            Timber.d("Authenticated user phone: %s", phone);
        }
        return phone;
    }

    /**
     * Gets the name of the currently authenticated contact.
     *
     * @return The authenticated contact name, or null if not set
     */
    @Nullable
    @Override
    public String getAuthenticatedContactName() {
        String contactName = sharedPreferences.getString(KEY_AUTH_CONTACT_NAME, null);
        if (contactName == null) {
            Timber.w("No authenticated contact name found");
        } else {
            Timber.d("Authenticated contact name: %s", contactName);
        }
        return contactName;
    }

    /**
     * Gets the name of the currently authenticated user.
     *
     * @return The authenticated user's name, or null if not set
     */
    @Nullable
    @Override
    public String getAuthenticatedUserAvatar() {
        String avatarUrl = sharedPreferences.getString(KEY_AUTH_USER_AVATAR, null);
        if (avatarUrl == null) {
            Timber.w("No authenticated user avatar found");
        } else {
            Timber.d("Authenticated user avatar: %s", avatarUrl);
        }
        return avatarUrl;
    }

    /**
     * Sets the name of the authenticated contact.
     *
     * @param contactName The name of the authenticated contact.
     */
    @Override
    public void setAuthenticatedContactName(String contactName) {
        sharedPreferences.edit().putString(KEY_AUTH_CONTACT_NAME, contactName).apply();
    }

    /**
     * Sets the phone number of the authenticated user.
     *
     * @param phone The phone number of the authenticated user.
     */
    @Override
    public void setAuthenticatedUserPhone(String phone) {
        sharedPreferences.edit().putString(KEY_AUTH_USER_PHONE, phone).apply();
    }

    /**
     * Sets the email of the authenticated user.
     *
     * @param email The email of the authenticated user.
     */
    @Override
    public void setAuthenticatedUserEmail(String email) {
        sharedPreferences.edit().putString(KEY_AUTH_USER_EMAIL, email).apply();
    }

    /**
     * Sets the ID of the authenticated user.
     *
     * @param userId The unique identifier of the authenticated user.
     */
    @Override
    public void setAuthenticatedUserId(String userId) {
        sharedPreferences.edit().putString(KEY_AUTH_USER_ID, userId).apply();
    }

    /**
     * Sets the avatar URL of the authenticated user.
     *
     * @param avatarUrl The URL of the user's avatar image.
     */
    @Override
    public void setAuthenticatedUserAvatar(String avatarUrl) {
        sharedPreferences.edit().putString(KEY_AUTH_USER_AVATAR, avatarUrl).apply();
    }

    /**
     * Clears all user preferences when a user logs in. This prevents data leakage between different
     * user sessions.
     */
    @Override
    public void clearAllPreferences() {
        Timber.d("Clearing all user preferences");

        // Log all preferences before clearing
        Map<String, ?> allPrefs = sharedPreferences.getAll();
        Timber.d("Current preferences before clearing (count: %d):", allPrefs.size());
        for (Map.Entry<String, ?> entry : allPrefs.entrySet()) {
            Timber.d("  Key: %s, Value: %s", entry.getKey(), entry.getValue());
        }

        sharedPreferences.edit().clear().apply();
        initDefaultValues(); // Reinitialize default values after clearing

        // Log all preferences after clearing and reinitializing defaults
        Map<String, ?> remainingPrefs = sharedPreferences.getAll();
        Timber.d("Remaining preferences after clearing (count: %d):", remainingPrefs.size());
        for (Map.Entry<String, ?> entry : remainingPrefs.entrySet()) {
            Timber.d("  Key: %s, Value: %s", entry.getKey(), entry.getValue());
        }

        Timber.d("User preferences cleared and defaults reinitialized");
    }
}
