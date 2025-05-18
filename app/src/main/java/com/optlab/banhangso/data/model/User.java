package com.optlab.banhangso.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;
import com.google.gson.annotations.SerializedName;
import com.optlab.banhangso.BR;

import java.util.Date;

/**
 * @noinspection LombokGetterMayBeUsed, LombokSetterMayBeUsed
 */
@IgnoreExtraProperties
public class User extends BaseObservable {
    @Exclude private String id;

    @SerializedName("contactName")
    private String contactName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("email")
    private String email;

    @SerializedName("imageUrl")
    private String imageUrl;

    @SerializedName("rid")
    private String role;

    @ServerTimestamp
    @SerializedName("createdAt")
    private Date createdAt;

    @ServerTimestamp
    @SerializedName("updatedAt")
    private Date updatedAt;

    public User() {}

    public User(
            String id,
            String contactName,
            String phone,
            String email,
            String imageUrl,
            String role,
            Date createdAt,
            Date updatedAt) {
        this.id = id;
        this.contactName = contactName;
        this.phone = phone;
        this.email = email;
        this.imageUrl = imageUrl;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
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
}
