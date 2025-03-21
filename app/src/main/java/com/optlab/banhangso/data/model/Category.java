package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class Category extends BaseObservable implements Cloneable {

    private int id;
    private String key;
    private String name;

    public Category(int id, String key, String name) {
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
        if (obj instanceof Category that) {
            return this.id == that.id && this.key.equals(that.key);
        } else {
            throw new IllegalArgumentException("Not instance of Category");
        }
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
