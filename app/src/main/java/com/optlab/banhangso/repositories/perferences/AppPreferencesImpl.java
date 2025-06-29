package com.optlab.banhangso.repositories.perferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import java.lang.reflect.Type;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton
public class AppPreferencesImpl implements AppPreferences {

  private final SharedPreferences sharedPreferences;
  private final Gson gson = new Gson();

  @Inject
  public AppPreferencesImpl(@NonNull Context context) {
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
      setSortOption(new SortOption<>(Category.SortField.NAME, true), KEY_CATEGORY_SORT_OPTION);
    }
  }

  @Override
  public void registerPreferencesChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    sharedPreferences.registerOnSharedPreferenceChangeListener(listener);
  }

  @Override
  public void unregisterPreferencesChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener) {
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener);
  }

  /**
   * Sets the sorting option for a specific entity type (products, brands, or categories).
   *
   * <p>Converts the sort option object to JSON and saves it in SharedPreferences using the provided
   * key to identify the entity type.
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
   * <p>The sort option is saved as a json string in the preference. The type of the sort option is
   * determined by the key passed to this method. The key is used to identify the sort option for
   * products, brands, or categories.
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
          case KEY_BRAND_SORT_OPTION -> new TypeToken<SortOption<Brand.SortField>>() {}.getType();
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
   * <p>Determines whether products are displayed in a grid layout (true) or a list layout (false).
   * If null is passed, defaults to list view (false).
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

  @Override
  public void setStore(@NonNull RoleStore roleStore) {
    String storeJson = gson.toJson(roleStore);
    sharedPreferences.edit().putString(KEY_CURRENT_STORE, storeJson).apply();
  }

  @Nullable @Override
  public RoleStore getStore() {
    String storeJson = sharedPreferences.getString(KEY_CURRENT_STORE, "");
    Timber.d("Raw JSON from SharedPreferences: '%s'", storeJson);
    if (storeJson.isEmpty()) {
      Timber.d("No store found in SharedPreferences, returning RoleStore.empty()");
      return RoleStore.empty();
    }
    RoleStore store = gson.fromJson(storeJson, new TypeToken<RoleStore>() {}.getType());
    Timber.d("Deserialized store from SharedPreferences: %s", store);
    return store;
  }

  /**
   * Clears all user preferences when a user logs in. This prevents data leakage between different
   * user sessions.
   */
  @Override
  public void clearPreferences() {
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

  @Override
  public void setUser(@NonNull User user) {
    String userJson = gson.toJson(user);
    sharedPreferences.edit().putString(KEY_CURRENT_USER, userJson).apply();
  }

  @Nullable @Override
  public User getUser() {
    String userJson = sharedPreferences.getString(KEY_CURRENT_USER, "");
    if (userJson.isEmpty()) {
      return null;
    }
    return gson.fromJson(userJson, new TypeToken<User>() {}.getType());
  }
}
