package com.optlab.banhangso.repositories.interfaces.preferences;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;

public interface AppPreferences {
  String PREFS_NAME = "user_preferences";
  String KEY_PRODUCT_SORT_OPTION = "product_sort_option";
  String KEY_BRAND_SORT_OPTION = "brand_sort_option";
  String KEY_CATEGORY_SORT_OPTION = "category_sort_option";
  String KEY_PRODUCT_LAYOUT_MODE = "product_layout_mode";
  String KEY_CURRENT_STORE = "current_store";
  String KEY_CURRENT_USER = "current_user";

  /**
   * Registers a listener for changes in the shared preferences.
   *
   * @param listener The listener to register
   */
  void registerPreferencesChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener);

  /**
   * Unregisters a listener for changes in the shared preferences.
   *
   * @param listener The listener to unregister
   */
  void unregisterPreferencesChangeListener(
      SharedPreferences.OnSharedPreferenceChangeListener listener);

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

  void setStore(@NonNull RoleStore roleStore);

  @Nullable RoleStore getStore();

  /**
   * Clears all user preferences. This should be called when a user logs in to prevent data leakage
   * between different user sessions.
   */
  void clearPreferences();

  void setUser(@NonNull User user);

  @Nullable User getUser();
}
