package com.optlab.banhangso.data.local.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.optlab.banhangso.domain.model.User;

import java.util.List;

public class StoresConverter {
    private static final Gson gson = new Gson();

    @TypeConverter
    public static String fromStores(List<User.Store> stores) {
        return stores == null ? null : gson.toJson(stores);
    }

    @TypeConverter
    public static List<User.Store> toStores(String s) {
        return s == null ? null : gson.fromJson(s, new TypeToken<List<User.Store>>() {
        }.getType());
    }
}
