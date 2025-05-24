package com.optlab.banhangso.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.optlab.banhangso.data.local.entity.converter.DateConverter;
import com.optlab.banhangso.data.local.entity.converter.StoresConverter;
import com.optlab.banhangso.data.model.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@TypeConverters({DateConverter.class, StoresConverter.class})
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "contact_name")
    private String contactName;

    @ColumnInfo(name = "phone")
    private String phone;

    @ColumnInfo(name = "email")
    private String email;

    @ColumnInfo(name = "image_url")
    private String imageUrl;

    @ColumnInfo(name = "stores")
    private List<User.Store> stores;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public UserEntity() {
    }

    @Ignore
    public UserEntity(
            @NonNull String id,
            String contactName,
            String phone,
            String email,
            String imageUrl,
            List<User.Store> stores,
            Date createdAt,
            Date updatedAt) {
        this.id = id;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.stores = stores;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @NonNull
    public List<User.Store> getStores() {
        return stores;
    }

    public void setStores(@NonNull List<User.Store> stores) {
        this.stores = stores;
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
        if (obj instanceof UserEntity that) {
            if (this == that) {
                return true;
            }
            return Objects.equals(this.id, that.id)
                    && Objects.equals(this.contactName, that.contactName)
                    && Objects.equals(this.phone, that.phone)
                    && Objects.equals(this.email, that.email)
                    && Objects.equals(this.imageUrl, that.imageUrl)
                    && Objects.equals(this.stores, that.stores)
                    && Objects.equals(this.createdAt, that.createdAt)
                    && Objects.equals(this.updatedAt, that.updatedAt);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(contactName, phone, email, imageUrl, stores, createdAt, updatedAt);
    }
}
