package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;


import java.util.Comparator;

public class Product extends BaseObservable implements Cloneable {

    public enum SortField {
        NAME("Tên A -> Z", "Tên Z -> A"),
        SELLING_PRICE("Giá từ thấp tới cao", "Giá từ cao tới thấp");

        private final String ascendingName;
        private final String descendingName;

        SortField(String ascendingName, String descendingName) {
            this.ascendingName = ascendingName;
            this.descendingName = descendingName;
        }

        public String getDisplayName(boolean isAscending) {
            return isAscending ? ascendingName : descendingName;
        }
    }

    public static Comparator<Product> getComparator(SortField field, boolean isAscending) {
        Comparator<Product> comparator = switch (field) {
            case NAME -> Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER);
            case SELLING_PRICE -> Comparator.comparingDouble(Product::getSellingPrice);
        };

        return isAscending ? comparator : comparator.reversed();
    }

    private final long id;
    private String key;
    private String barcode;
    private Category category;
    private Brand brand;

    private String name;
    private double purchasePrice;
    private double sellingPrice;
    private String avatarUrl;
    private int stockQuantity;
    private int actualStockQuantity;
    private String description;
    private ProductStatus status;
    private double discountPrice;
    private String note;

    private Product(Builder builder) {
        this.id = builder.id;
        this.key = builder.key;
        this.barcode = builder.barcode;
        this.category = builder.category;
        this.brand = builder.brandId;
        this.name = builder.name;
        this.purchasePrice = builder.purchasePrice;
        this.sellingPrice = builder.sellingPrice;
        this.avatarUrl = builder.avatarUrl;
        this.stockQuantity = builder.stockQuantity;
        this.actualStockQuantity = builder.actualStockQuantity;
        this.description = builder.description;
        this.status = builder.status;
        this.discountPrice = builder.discountPercentage;
        this.note = builder.note;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public String getBarcode() {
        return barcode;
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
    public int getActualStockQuantity() {
        return actualStockQuantity;
    }

    public void setActualStockQuantity(int actualStockQuantity) {
        this.actualStockQuantity = actualStockQuantity;
        notifyPropertyChanged(BR.actualStockQuantity);
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
    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
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

    public enum ProductStatus {
        IN_STOCK((byte) 1),
        OUT_STOCK((byte) 0);

        private final byte value;

        ProductStatus(byte value) {
            this.value = value;
        }

        public static ProductStatus fromValue(byte value) {
            for (ProductStatus status : ProductStatus.values()) {
                if (status.value == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status value");
        }

        public byte getValue() {
            return value;
        }
    }

    public static class Builder {
        private final long id;
        private String key = "";
        private String barcode = "";
        private Category category;
        private Brand brandId;

        private String name = "";
        private double purchasePrice;
        private double sellingPrice;
        private String avatarUrl = "";
        private int stockQuantity;
        private int actualStockQuantity;
        private String description = "";
        private ProductStatus status = ProductStatus.OUT_STOCK;
        private double discountPercentage;
        private String note = "";

        public Builder(long id) {
            this.id = id;
        }

        public Builder key(String key) {
            this.key = key != null ? key : "";
            return this;
        }

        public Builder barcode(String barcode) {
            this.barcode = barcode != null ? barcode : "";
            return this;
        }

        public Builder category(Category category) {
            this.category = category;
            return this;
        }

        public Builder brand(Brand brand) {
            this.brandId = brand;
            return this;
        }

        public Builder name(String name) {
            this.name = name != null ? name : "";
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

        public Builder avatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl != null ? avatarUrl : "";
            return this;
        }

        public Builder stockQuantity(int stockQuantity) {
            this.stockQuantity = stockQuantity;
            return this;
        }

        public Builder actualStockQuantity(int actualStockQuantity) {
            this.actualStockQuantity = actualStockQuantity;
            return this;
        }

        public Builder description(String description) {
            this.description = description != null ? description : "";
            return this;
        }

        public Builder status(ProductStatus status) {
            this.status = status != null ? status : ProductStatus.OUT_STOCK;
            return this;
        }

        public Builder discountPrice(double discountPrice) {
            this.discountPercentage = discountPrice;
            return this;
        }

        public Builder note(String note) {
            this.note = note != null ? note : "";
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}