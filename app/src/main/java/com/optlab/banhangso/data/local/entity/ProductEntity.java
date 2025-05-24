package com.optlab.banhangso.data.local.entity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.android.gms.common.internal.Objects;
import com.optlab.banhangso.data.local.entity.converter.BrandConverter;
import com.optlab.banhangso.data.local.entity.converter.CategoryConverter;
import com.optlab.banhangso.data.local.entity.converter.DateConverter;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;

import java.util.Date;

@Entity
@TypeConverters({DateConverter.class, CategoryConverter.class, BrandConverter.class})
public class ProductEntity {
    @PrimaryKey(autoGenerate = true)
    private int _id;

    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "category")
    private Category category;

    @ColumnInfo(name = "brand")
    private Brand brand;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "barcode")
    private String barcode;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "stock_quantity")
    private int stockQuantity;

    @ColumnInfo(name = "purchase_price")
    private double purchasePrice;

    @ColumnInfo(name = "selling_price")
    private double sellingPrice;

    @ColumnInfo(name = "status")
    private boolean status;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    @ColumnInfo(name = "updated_at")
    private Date updatedAt;

    public ProductEntity() {
    }

    @Ignore
    public ProductEntity(
            String id,
            Category category,
            Brand brand,
            String name,
            String note,
            String barcode,
            String description,
            int stockQuantity,
            double purchasePrice,
            double sellingPrice,
            boolean status,
            Date createdAt,
            Date updatedAt) {
        this.id = id;
        this.category = category;
        this.brand = brand;
        this.name = name;
        this.note = note;
        this.barcode = barcode;
        this.description = description;
        this.stockQuantity = stockQuantity;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.status = status;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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
        return "ProductEntity{"
                + "id='"
                + id
                + '\''
                + ", category="
                + category
                + ", brand="
                + brand
                + ", name='"
                + name
                + '\''
                + ", note='"
                + note
                + '\''
                + ", barcode='"
                + barcode
                + '\''
                + ", description='"
                + description
                + '\''
                + ", stockQuantity="
                + stockQuantity
                + ", purchasePrice="
                + purchasePrice
                + ", sellingPrice="
                + sellingPrice
                + ", status="
                + status
                + ", createdAt="
                + createdAt
                + ", updatedAt="
                + updatedAt
                + '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof ProductEntity that) {
            if (this == that) {
                return true;
            }
            return Objects.equal(this.id, that.id)
                    && this.status == that.status
                    && this.stockQuantity == that.stockQuantity
                    && Objects.equal(this.category, that.category)
                    && Objects.equal(this.brand, that.brand)
                    && Objects.equal(this.name, that.name)
                    && Objects.equal(this.note, that.note)
                    && Objects.equal(this.barcode, that.barcode)
                    && Objects.equal(this.description, that.description)
                    && Double.compare(this.purchasePrice, that.purchasePrice) == 0
                    && Double.compare(this.sellingPrice, that.sellingPrice) == 0
                    && Objects.equal(this.createdAt, that.createdAt)
                    && Objects.equal(this.updatedAt, that.updatedAt);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(
                id,
                category,
                brand,
                name,
                note,
                barcode,
                description,
                stockQuantity,
                purchasePrice,
                sellingPrice,
                status,
                createdAt,
                updatedAt);
    }
}
