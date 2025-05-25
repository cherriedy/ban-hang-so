package com.optlab.banhangso.data.remote.dto;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@IgnoreExtraProperties
public class UserDto {
    private String id;

    @SerializedName("contactName")
    private String contactName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("stores")
    private List<Store> stores;

    @ServerTimestamp
    @SerializedName("createdAt")
    private Date createdAt;

    @ServerTimestamp
    @SerializedName("updatedAt")
    private Date updatedAt;

    public UserDto() {}

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

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
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

    @NonNull
    @Override
    public String toString() {
        return "UserDto{"
                + "id='"
                + id
                + '\''
                + ", contactName='"
                + contactName
                + '\''
                + ", phone='"
                + phone
                + '\''
                + ", email='"
                + email
                + '\''
                + ", imageUrl='"
                + imageUrl
                + '\''
                + ", stores="
                + stores
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UserDto that) {
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
        return Objects.hash(id, contactName, phone, email, imageUrl, stores, createdAt, updatedAt);
    }

    @IgnoreExtraProperties
    public static class Store {
        public static final String ADMIN = "ADMIN";
        public static final String STAFF = "STAFF";

        @SerializedName("id")
        private String id;

        @SerializedName("rid")
        private String role = ADMIN;

        public Store() {}

        public Store(String id) {
            this.id = id;
        }

        public Store(String id, String role) {
            this.id = id;
            this.role = role;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    public static class Builder {
        private String contactName;
        private String phone;
        private String email;
        private String imageUrl;
        private List<Store> stores;

        public Builder setContactName(String contactName) {
            this.contactName = contactName;
            return this;
        }

        public Builder setPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setStores(List<Store> stores) {
            this.stores = stores;
            return this;
        }

        public UserDto build() {
            UserDto user = new UserDto();
            user.contactName = contactName;
            user.phone = phone;
            user.email = email;
            user.imageUrl = imageUrl;
            user.stores = stores;
            return user;
        }
    }
}
