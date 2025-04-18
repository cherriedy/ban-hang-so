package com.optlab.banhangso.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import javax.inject.Inject;

public class UserPreferenceManager {
    public static final String KEY_PRODUCT_SORT_OPTION = "product_sort_option";
    public static final String KEY_BRAND_SORT_OPTION = "brand_sort_option";
    public static final String KEY_CATEGORY_SORT_OPTION = "category_sort_option";
    public static final String KEY_PRODUCT_LAYOUT_MODE = "product_layout_mode";

    private static final String PREFS_NAME = "user_preferences";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    @Inject
    public UserPreferenceManager(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

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
    public SortOption<?> getSortOption(String key) {
        // Get sort option json from preference.
        String sortOptionJson = sharedPreferences.getString(key, null);

        // Get the type of sort option to prevent unchecked conversion.
        Type type =
                switch (key) {
                    case KEY_PRODUCT_SORT_OPTION ->
                            new TypeToken<SortOption<Product.SortField>>() {}.getType();
                    case KEY_BRAND_SORT_OPTION ->
                            new TypeToken<SortOption<Brand.SortField>>() {}.getType();
                    case KEY_CATEGORY_SORT_OPTION ->
                            new TypeToken<SortOption<Product.SortField>>() {}.getType();
                    default -> throw new IllegalArgumentException("Invalid key: " + key);
                };

        // Convert the sort option json to object.
        return gson.fromJson(sortOptionJson, type);
    }

    public void setLayoutMode(Boolean isGrid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isGrid == null) isGrid = false;
        editor.putBoolean(KEY_PRODUCT_LAYOUT_MODE, isGrid);
        editor.apply();
    }

    public boolean getLayoutMode() {
        return sharedPreferences.getBoolean(KEY_PRODUCT_LAYOUT_MODE, false);
    }
}
