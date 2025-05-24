package com.optlab.banhangso.data.local.entity.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.data.model.Brand;

public class BrandConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromBrand(Brand brand) {
        return brand == null ? null : gson.toJson(brand);
    }

    @TypeConverter
    public static Brand toBrand(String s) {
        return s == null ? null : gson.fromJson(s, new TypeToken<Brand>() {
        }.getType());
    }
}
