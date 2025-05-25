package com.optlab.banhangso.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.optlab.banhangso.R;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@IgnoreExtraProperties
public class User extends BaseObservable {
    private String id;
    private String contactName;
    private String phone;
    private String email;
    private String imageUrl;
    private List<Store> stores;
    private Date createdAt;
    private Date updatedAt;

    public User() {}

    public User(
            String id,
            String contactName,
            String phone,
            String email,
            String imageUrl,
            List<Store> stores,
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

    @Bindable
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyPropertyChanged(BR.id);
    }

    @Bindable
    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
        notifyPropertyChanged(BR.name);
    }

    @Bindable
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        notifyPropertyChanged(BR.phone);
    }

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
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
        return "User{"
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
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof User that) {
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

    public static class Store extends BaseObservable {
        public static final String ADMIN = "ADMIN";
        public static final String STAFF = "STAFF";

        private String id;
        private String role = ADMIN;

        public Store() {}

        public Store(String id) {
            this.id = id;
        }

        public Store(String id, String role) {
            this.id = id;
            this.role = role;
        }

        @Bindable
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
            notifyPropertyChanged(BR.id);
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        @StringRes
        public int getDisplayRoleResId() {
            return role.equals(ADMIN) ? R.string.role_store_owner : R.string.role_store_staff;
        }
    }

    public static class Builder {
        private String id;
        private String contactName;
        private String phone;
        private String email;
        private String imageUrl;
        private List<Store> stores;
        private Date createdAt;
        private Date updatedAt;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

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

        public Builder setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public User build() {
            return new User(id, contactName, phone, email, imageUrl, stores, createdAt, updatedAt);
        }
    }
}
