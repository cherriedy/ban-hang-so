package com.optlab.banhangso.data.local.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.domain.model.Category;

public class CategoryConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromCategory(Category category) {
        return category == null ? null : gson.toJson(category);
    }

    @TypeConverter
    public static Category toCategory(String s) {
        return s == null ? null : gson.fromJson(s, new TypeToken<Category>() {
        }.getType());
    }
}
