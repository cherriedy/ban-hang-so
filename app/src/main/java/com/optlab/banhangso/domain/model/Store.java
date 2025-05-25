package com.optlab.banhangso.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.optlab.banhangso.data.local.entity.StoreEntity;
import com.optlab.banhangso.data.remote.dto.StoreDto;

import java.util.Date;
import java.util.Objects;

/**
 * Domain model for Store used in the UI and business logic This class serves as the data
 * representation for use in the application
 */
public class Store extends BaseObservable {
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private Date createdAt;
    private Date updatedAt;

    public Store() {
    }

    public Store(
            String id, String name, String description, String imageUrl, Date createdAt, Date updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Convert this domain model to a DTO for Firebase operations
     *
     * @return the StoreDto for Firebase
     */
    public StoreDto toDto() {
        StoreDto dto = new StoreDto();
        dto.setId(this.id);
        dto.setName(this.name);
        dto.setDescription(this.description);
        dto.setImageUrl(this.imageUrl);
        dto.setCreatedAt(this.createdAt);
        dto.setUpdatedAt(this.updatedAt);
        return dto;
    }

    /**
     * Convert this domain model to an entity for local storage
     *
     * @return the StoreEntity for Room
     */
    public StoreEntity toEntity() {
        StoreEntity entity = new StoreEntity();
        entity.setId(this.id);
        entity.setName(this.name);
        entity.setDescription(this.description);
        entity.setImageUrl(this.imageUrl);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        return entity;
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

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
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
        return "Store{"
                + "id='"
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
        if (obj instanceof Store that) {
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
        return Objects.hash(id, name, description, imageUrl, createdAt, updatedAt);
    }
}
