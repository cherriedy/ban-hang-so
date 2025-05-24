package com.optlab.banhangso.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.optlab.banhangso.data.local.entity.converter.DateConverter;
import com.optlab.banhangso.data.model.Store;

import java.util.Date;
import java.util.Objects;

@Entity
@TypeConverters({DateConverter.class})
public class StoreEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    private String id = "";

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public StoreEntity() {}

    @Ignore
    public StoreEntity(
            @NonNull String id,
            String name,
            String description,
            String imageUrl,
            Date createdAt,
            Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Convert this entity to a domain model
     *
     * @return the Store domain model
     */
    public Store toDomainModel() {
        Store store = new Store();
        store.setId(this.id);
        store.setName(this.name);
        store.setDescription(this.description);
        store.setImageUrl(this.imageUrl);
        store.setCreatedAt(this.createdAt);
        store.setUpdatedAt(this.updatedAt);
        return store;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @NonNull
    @Override
    public String toString() {
        return "StoreEntity{"
                + "_id="
                + _id
                + ", id='"
                + id
                + '\''
                + ", name='"
                + name
                + '\''
                + ", description='"
                + description
                + '\''
                + ", imageUrl='"
                + imageUrl
                + '\''
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof StoreEntity that) {
            if (this == that) {
                return true;
            }
            return Objects.equals(this.id, that.id)
                    && Objects.equals(this.name, that.name)
                    && Objects.equals(this.description, that.description)
                    && Objects.equals(this.imageUrl, that.imageUrl)
                    && Objects.equals(this.createdAt, that.createdAt)
                    && Objects.equals(this.updatedAt, that.updatedAt);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, imageUrl, createdAt, updatedAt);
    }
}
