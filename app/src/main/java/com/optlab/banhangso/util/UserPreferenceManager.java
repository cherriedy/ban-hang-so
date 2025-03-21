package com.optlab.banhangso.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class UserPreferenceManager {
    private static final String PREFS_NAME = "user_preferences";
    private static final String KEY_SORT_OPTION = "sort_option";
    private static final String KEY_LAYOUT_MODE = "layout_mode";

    private final SharedPreferences sharedPreferences;
    private final Gson gson = new Gson();

    public UserPreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveSortOption(SortOption<Product.SortField> sortOption) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Convert the sort option object to json.
        String sortOptionJson = gson.toJson(sortOption);

        // Save the sort option json to preference.
        editor.putString(KEY_SORT_OPTION, sortOptionJson);

        editor.apply();
    }

    public SortOption<Product.SortField> getSortOption() {
        // Get sort option json from preference.
        String sortOptionJson = sharedPreferences.getString(KEY_SORT_OPTION, null);

        // Get the type of sort option to prevent unchecked conversion.
        Type type = new TypeToken<SortOption<Product.SortField>>() {
        }.getType();

        // Convert the sort option json to object.
        SortOption<Product.SortField> sortOption = gson.fromJson(sortOptionJson, type);

        return sortOption != null ? sortOption : new SortOption<>(Product.SortField.NAME, false);
    }

    public void saveLayoutMode(Boolean isGrid) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (isGrid == null) isGrid = false;
        editor.putBoolean(KEY_LAYOUT_MODE, isGrid);
        editor.apply();
    }

    public boolean getLayoutMode() {
        return sharedPreferences.getBoolean(KEY_LAYOUT_MODE, false);
    }
}
