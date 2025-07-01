package com.optlab.banhangso.models.domain;

import com.optlab.banhangso.models.application.SortOption;
import java.util.Date;

public class Product {
  public Product(
      String id,
      String barcode,
      Category category,
      Brand brand,
      String name,
      double purchasePrice,
      double sellingPrice,
      String avatarUrl,
      int stockQuantity,
      String description,
      boolean status,
      double discountPrice,
      String note,
      Date createdAt,
      Date updatedAt) {
    this.id = id;
    this.barcode = barcode;
    this.category = category;
    this.brand = brand;
    this.name = name;
    this.purchasePrice = purchasePrice;
    this.sellingPrice = sellingPrice;
    this.avatarUrl = avatarUrl;
    this.stockQuantity = stockQuantity;
    this.description = description;
    this.status = status;
    this.discountPrice = discountPrice;
    this.note = note;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public Product() {}

  public static ProductBuilder builder() {
    return new ProductBuilder();
  }

  public String getId() {
    return this.id;
  }

  public String getBarcode() {
    return this.barcode;
  }

  public Category getCategory() {
    return this.category;
  }

  public Brand getBrand() {
    return this.brand;
  }

  public String getName() {
    return this.name;
  }

  public double getPurchasePrice() {
    return this.purchasePrice;
  }

  public double getSellingPrice() {
    return this.sellingPrice;
  }

  public String getAvatarUrl() {
    return this.avatarUrl;
  }

  public int getStockQuantity() {
    return this.stockQuantity;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean isStatus() {
    return this.status;
  }

  public double getDiscountPrice() {
    return this.discountPrice;
  }

  public String getNote() {
    return this.note;
  }

  public Date getCreatedAt() {
    return this.createdAt;
  }

  public Date getUpdatedAt() {
    return this.updatedAt;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public void setBrand(Brand brand) {
    this.brand = brand;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  public void setSellingPrice(double sellingPrice) {
    this.sellingPrice = sellingPrice;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public void setStockQuantity(int stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setStatus(boolean status) {
    this.status = status;
  }

  public void setDiscountPrice(double discountPrice) {
    this.discountPrice = discountPrice;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Date updatedAt) {
    this.updatedAt = updatedAt;
  }

  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof Product)) return false;
    final Product other = (Product) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$id = this.getId();
    final Object other$id = other.getId();
    if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
    final Object this$barcode = this.getBarcode();
    final Object other$barcode = other.getBarcode();
    if (this$barcode == null ? other$barcode != null : !this$barcode.equals(other$barcode))
      return false;
    final Object this$category = this.getCategory();
    final Object other$category = other.getCategory();
    if (this$category == null ? other$category != null : !this$category.equals(other$category))
      return false;
    final Object this$brand = this.getBrand();
    final Object other$brand = other.getBrand();
    if (this$brand == null ? other$brand != null : !this$brand.equals(other$brand)) return false;
    final Object this$name = this.getName();
    final Object other$name = other.getName();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    if (Double.compare(this.getPurchasePrice(), other.getPurchasePrice()) != 0) return false;
    if (Double.compare(this.getSellingPrice(), other.getSellingPrice()) != 0) return false;
    final Object this$avatarUrl = this.getAvatarUrl();
    final Object other$avatarUrl = other.getAvatarUrl();
    if (this$avatarUrl == null ? other$avatarUrl != null : !this$avatarUrl.equals(other$avatarUrl))
      return false;
    if (this.getStockQuantity() != other.getStockQuantity()) return false;
    final Object this$description = this.getDescription();
    final Object other$description = other.getDescription();
    if (this$description == null
        ? other$description != null
        : !this$description.equals(other$description)) return false;
    if (this.isStatus() != other.isStatus()) return false;
    if (Double.compare(this.getDiscountPrice(), other.getDiscountPrice()) != 0) return false;
    final Object this$note = this.getNote();
    final Object other$note = other.getNote();
    if (this$note == null ? other$note != null : !this$note.equals(other$note)) return false;
    final Object this$createdAt = this.getCreatedAt();
    final Object other$createdAt = other.getCreatedAt();
    if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt))
      return false;
    final Object this$updatedAt = this.getUpdatedAt();
    final Object other$updatedAt = other.getUpdatedAt();
    if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt))
      return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof Product;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.getId();
    result = result * PRIME + ($id == null ? 43 : $id.hashCode());
    final Object $barcode = this.getBarcode();
    result = result * PRIME + ($barcode == null ? 43 : $barcode.hashCode());
    final Object $category = this.getCategory();
    result = result * PRIME + ($category == null ? 43 : $category.hashCode());
    final Object $brand = this.getBrand();
    result = result * PRIME + ($brand == null ? 43 : $brand.hashCode());
    final Object $name = this.getName();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final long $purchasePrice = Double.doubleToLongBits(this.getPurchasePrice());
    result = result * PRIME + (int) ($purchasePrice >>> 32 ^ $purchasePrice);
    final long $sellingPrice = Double.doubleToLongBits(this.getSellingPrice());
    result = result * PRIME + (int) ($sellingPrice >>> 32 ^ $sellingPrice);
    final Object $avatarUrl = this.getAvatarUrl();
    result = result * PRIME + ($avatarUrl == null ? 43 : $avatarUrl.hashCode());
    result = result * PRIME + this.getStockQuantity();
    final Object $description = this.getDescription();
    result = result * PRIME + ($description == null ? 43 : $description.hashCode());
    result = result * PRIME + (this.isStatus() ? 79 : 97);
    final long $discountPrice = Double.doubleToLongBits(this.getDiscountPrice());
    result = result * PRIME + (int) ($discountPrice >>> 32 ^ $discountPrice);
    final Object $note = this.getNote();
    result = result * PRIME + ($note == null ? 43 : $note.hashCode());
    final Object $createdAt = this.getCreatedAt();
    result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
    final Object $updatedAt = this.getUpdatedAt();
    result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
    return result;
  }

  public String toString() {
    return "Product(id="
        + this.getId()
        + ", barcode="
        + this.getBarcode()
        + ", category="
        + this.getCategory()
        + ", brand="
        + this.getBrand()
        + ", name="
        + this.getName()
        + ", purchasePrice="
        + this.getPurchasePrice()
        + ", sellingPrice="
        + this.getSellingPrice()
        + ", avatarUrl="
        + this.getAvatarUrl()
        + ", stockQuantity="
        + this.getStockQuantity()
        + ", description="
        + this.getDescription()
        + ", status="
        + this.isStatus()
        + ", discountPrice="
        + this.getDiscountPrice()
        + ", note="
        + this.getNote()
        + ", createdAt="
        + this.getCreatedAt()
        + ", updatedAt="
        + this.getUpdatedAt()
        + ")";
  }

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

  private String id;
  private String barcode;
  private Category category;
  private Brand brand;
  private String name;
  private double purchasePrice;
  private double sellingPrice;
  private String avatarUrl;
  private int stockQuantity;
  private String description;
  private boolean status;
  private double discountPrice;
  private String note;
  private Date createdAt;
  private Date updatedAt;

  public static class ProductBuilder {
    private String id;
    private String barcode;
    private Category category;
    private Brand brand;
    private String name;
    private double purchasePrice;
    private double sellingPrice;
    private String avatarUrl;
    private int stockQuantity;
    private String description;
    private boolean status;
    private double discountPrice;
    private String note;
    private Date createdAt;
    private Date updatedAt;

    ProductBuilder() {}

    public ProductBuilder id(String id) {
      this.id = id;
      return this;
    }

    public ProductBuilder barcode(String barcode) {
      this.barcode = barcode;
      return this;
    }

    public ProductBuilder category(Category category) {
      this.category = category;
      return this;
    }

    public ProductBuilder brand(Brand brand) {
      this.brand = brand;
      return this;
    }

    public ProductBuilder name(String name) {
      this.name = name;
      return this;
    }

    public ProductBuilder purchasePrice(double purchasePrice) {
      this.purchasePrice = purchasePrice;
      return this;
    }

    public ProductBuilder sellingPrice(double sellingPrice) {
      this.sellingPrice = sellingPrice;
      return this;
    }

    public ProductBuilder avatarUrl(String avatarUrl) {
      this.avatarUrl = avatarUrl;
      return this;
    }

    public ProductBuilder stockQuantity(int stockQuantity) {
      this.stockQuantity = stockQuantity;
      return this;
    }

    public ProductBuilder description(String description) {
      this.description = description;
      return this;
    }

    public ProductBuilder status(boolean status) {
      this.status = status;
      return this;
    }

    public ProductBuilder discountPrice(double discountPrice) {
      this.discountPrice = discountPrice;
      return this;
    }

    public ProductBuilder note(String note) {
      this.note = note;
      return this;
    }

    public ProductBuilder createdAt(Date createdAt) {
      this.createdAt = createdAt;
      return this;
    }

    public ProductBuilder updatedAt(Date updatedAt) {
      this.updatedAt = updatedAt;
      return this;
    }

    public Product build() {
      return new Product(
          this.id,
          this.barcode,
          this.category,
          this.brand,
          this.name,
          this.purchasePrice,
          this.sellingPrice,
          this.avatarUrl,
          this.stockQuantity,
          this.description,
          this.status,
          this.discountPrice,
          this.note,
          this.createdAt,
          this.updatedAt);
    }

    public String toString() {
      return "Product.ProductBuilder(id="
          + this.id
          + ", barcode="
          + this.barcode
          + ", category="
          + this.category
          + ", brand="
          + this.brand
          + ", name="
          + this.name
          + ", purchasePrice="
          + this.purchasePrice
          + ", sellingPrice="
          + this.sellingPrice
          + ", avatarUrl="
          + this.avatarUrl
          + ", stockQuantity="
          + this.stockQuantity
          + ", description="
          + this.description
          + ", status="
          + this.status
          + ", discountPrice="
          + this.discountPrice
          + ", note="
          + this.note
          + ", createdAt="
          + this.createdAt
          + ", updatedAt="
          + this.updatedAt
          + ")";
    }
  }
}
