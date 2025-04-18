package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.PropertyName;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

@IgnoreExtraProperties
public class Product extends BaseObservable implements Cloneable {
    public enum SortField implements SortOption.Displayable {
        NAME("Tên A -> Z", "Tên Z -> A"),
        SELLING_PRICE("Giá từ thấp tới cao", "Giá từ cao tới thấp");

        private final String ascendingName;
        private final String descendingName;

        SortField(String ascendingName, String descendingName) {
            this.ascendingName = ascendingName;
            this.descendingName = descendingName;
        }

        @Override
        public String getDisplayName(boolean isAscending) {
            return isAscending ? ascendingName : descendingName;
        }
    }

    @Exclude private String id;
    private String barcode;
    private Category category;
    private Brand brand;
    private String name;
    private double purchasePrice;
    private double sellingPrice;
    private String avatarUrl;

    @PropertyName("stock")
    private int stockQuantity;

    private String description;
    private boolean status;
    private double discountPrice;
    private String note;
    @ServerTimestamp private Date createdAt;
    @ServerTimestamp private Date updatedAt;

    public Product() {}

    public static Product empty() {
        return new Builder("")
                .name("")
                .barcode("")
                .category(Category.empty())
                .brand(Brand.empty())
                .purchasePrice(0)
                .sellingPrice(0)
                .avatarUrl("")
                .stockQuantity(0)
                .description("")
                .status(true)
                .discountPrice(0)
                .note("")
                .build();
    }

    private Product(@NonNull Builder builder) {
        this.id = builder.id;
        this.barcode = builder.barcode;
        this.category = builder.category;
        this.brand = builder.brand;
        this.name = builder.name;
        this.purchasePrice = builder.purchasePrice;
        this.sellingPrice = builder.sellingPrice;
        this.avatarUrl = builder.avatarUrl;
        this.stockQuantity = builder.stockQuantity;
        this.description = builder.description;
        this.status = builder.status;
        this.discountPrice = builder.discountPrice;
        this.note = builder.note;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof Product that) {
            if (this == obj) return true;
            return id.equals(that.id)
                    && barcode.equals(that.barcode)
                    && category.equals(that.category)
                    && brand.equals(that.brand)
                    && name.equals(that.name)
                    && purchasePrice == that.purchasePrice
                    && sellingPrice == that.sellingPrice
                    && avatarUrl.equals(that.avatarUrl)
                    && stockQuantity == that.stockQuantity
                    && description.equals(that.description)
                    && status == that.status
                    && discountPrice == that.discountPrice
                    && note.equals(that.note);
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "Product{"
                + "id='"
                + id
                + '\''
                + ", barcode='"
                + barcode
                + '\''
                + ", category="
                + category
                + ", brand="
                + brand
                + ", name='"
                + name
                + '\''
                + ", purchasePrice="
                + purchasePrice
                + ", sellingPrice="
                + sellingPrice
                + ", avatarUrl='"
                + avatarUrl
                + '\''
                + ", stockQuantity="
                + stockQuantity
                + ", description='"
                + description
                + '\''
                + ", status="
                + status
                + ", discountPrice="
                + discountPrice
                + ", note='"
                + note
                + '\''
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    @Bindable
    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        notifyPropertyChanged(BR.category);
    }

    @Bindable
    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
        notifyPropertyChanged(BR.brand);
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
    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
        notifyPropertyChanged(BR.purchasePrice);
    }

    @Bindable
    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
        notifyPropertyChanged(BR.sellingPrice);
    }

    @Bindable
    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        notifyPropertyChanged(BR.stockQuantity);
    }

    @Bindable
    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        notifyPropertyChanged(BR.avatarUrl);
    }

    @Bindable
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        notifyPropertyChanged(BR.description);
    }

    @Bindable
    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        notifyPropertyChanged(BR.status);
    }

    @Bindable
    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
        notifyPropertyChanged(BR.discountPrice);
    }

    @Bindable
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
        notifyPropertyChanged(BR.note);
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

    public static class Builder {
        private String id;
        private String barcode = "";
        private Category category;
        private Brand brand;
        private String name = "";
        private double purchasePrice;
        private double sellingPrice;
        private String avatarUrl = "";
        private int stockQuantity;
        private String description = "";
        private boolean status;
        private double discountPrice;
        private String note = "";

        public Builder(String id) {
            this.id = id;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder barcode(String barcode) {
            this.barcode = barcode;
            return this;
        }

        public Builder brand(Brand brand) {
            this.brand = brand;
            return this;
        }

        public Builder purchasePrice(double purchasePrice) {
            this.purchasePrice = purchasePrice;
            return this;
        }

        public Builder sellingPrice(double sellingPrice) {
            this.sellingPrice = sellingPrice;
            return this;
        }

        public Builder discountPrice(double discountPrice) {
            this.discountPrice = discountPrice;
            return this;
        }

        public Builder stockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder status(boolean status) {
            this.status = status;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder note(String note) {
            this.note = note;
            return this;
        }

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
