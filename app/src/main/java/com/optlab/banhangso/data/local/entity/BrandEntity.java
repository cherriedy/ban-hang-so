package com.optlab.banhangso.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.common.internal.Objects;
import com.optlab.banhangso.data.local.converter.DateConverter;
import com.optlab.banhangso.domain.model.Brand;

import java.util.Date;

@Entity
@TypeConverters(DateConverter.class)
public class BrandEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public BrandEntity() {}

    @Ignore
    public BrandEntity(@NonNull String id, String name, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static BrandEntity fromModel(Brand brand) {
        if (brand == null) return null;
        return new BrandEntity(
                brand.getId(), brand.getName(), brand.getCreatedAt(), brand.getUpdatedAt());
    }

    public Brand toModel() {
        Brand brand = new Brand(id, name);
        brand.setCreatedAt(createdAt);
        brand.setUpdatedAt(updatedAt);
        return brand;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof BrandEntity that) {
            if (this == that) return true;
            return Objects.equal(this.id, that.id)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.createdAt, that.createdAt)
                    && Objects.equal(this.updatedAt, that.updatedAt);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, createdAt, updatedAt);
    }
}
