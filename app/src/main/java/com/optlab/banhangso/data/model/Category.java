package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Category extends BaseObservable implements Cloneable {
    @Exclude private String id;
    private String name;

    @ServerTimestamp private Date createdAt;
    @ServerTimestamp private Date updatedAt;

    public Category() {}

    public Category(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Category empty() {
        return new Category("", "");
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Category that) {
            if (this == that) return true;
            return this.id.equals(that.id) && this.name.equals(that.name);
        } else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Bindable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        notifyPropertyChanged(BR.name);
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isEmpty() {
        return id.isEmpty() && name.isEmpty();
    }
}
