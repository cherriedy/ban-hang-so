package com.optlab.banhangso.repositories.perferences;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import java.util.Map;
import timber.log.Timber;

@Deprecated
public class AppPreferencesImpl implements AppPreferences {

  private final SharedPreferences sharedPreferences;
  private final Gson gson = new Gson();

  public AppPreferencesImpl(@NonNull Context context) {
    sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
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
