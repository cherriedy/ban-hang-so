package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class Brand extends BaseObservable implements Cloneable {

    private int id;
    private String key;
    private String name;

    public Brand(int id, String key, String name) {
        this.id = id;
        this.key = key;
        this.name = name;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // If o is null or isn't instance of Brand, return false.
        if (obj == null || !(obj instanceof Brand)) return false;

        // If the object is compared with itself, then return true.
        if (obj == this) return true;

        Brand brand = (Brand) obj;

        // Return true, if and only if all attributes are the same.
        return brand.id == id && brand.key.equals(key) && brand.name.equals(name);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }
}
