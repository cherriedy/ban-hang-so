package com.optlab.banhangso.models.remote;

import androidx.annotation.Nullable;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TransactionRecordFirebaseObject {

  @Nullable private String id;
  @Nullable private Customer customer;
  @Nullable private Staff staff;
  @Nullable private Integer totalItems;
  @Nullable private Double totalSellingPrices;
  @Nullable private Double totalPurchasePrices;
  @Nullable private Double totalDiscountPrices;
  @Nullable private Double finalPrices;
  @Nullable private String paymentMethod;
  @Nullable private List<Item> items;
  @Nullable private String note;
  @Nullable private Date createdAt;

  @Data
  @NoArgsConstructor
  public static class Customer {

    @Nullable private String id;
    @Nullable private String name;
    @Nullable private String phone;
    @Nullable private String email;
  }

  @Data
  @NoArgsConstructor
  public static class Staff {

    @Nullable private String id;
    @Nullable private String name;
    @Nullable private String phone;
    @Nullable private String email;
    @Nullable private String role;
  }

  @Data
  @NoArgsConstructor
  public static class Item {

    @Nullable private String id;
    @Nullable private String name;
    @Nullable private String thumbnailUrl;
    @Nullable private Double sellingPrice;
    @Nullable private Double discountPrice;
    @Nullable private Double purchasePrice;
    @Nullable private Integer quantity;
    @Nullable private String barcode;
    @Nullable private Brand brand;
    @Nullable private Category category;

    @Data
    @NoArgsConstructor
    public static class Brand {

      @Nullable private String id;
      @Nullable private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Category {

      @Nullable private String id;
      @Nullable private String name;
    }
  }
}
