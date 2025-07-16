package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.optlab.banhangso.models.domain.TransactionRecord;
import com.optlab.banhangso.models.remote.TransactionRecordFirebaseObject;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class TransactionRecordFirebaseObjectMapper {

  @NotNull public static TransactionRecord toDomain(
      @NonNull TransactionRecordFirebaseObject transactionRecordFirebaseObject) {
    List<TransactionRecord.Item> items = mapItems(transactionRecordFirebaseObject.getItems());

    TransactionRecord.Customer customer =
        mapCustomer(transactionRecordFirebaseObject.getCustomer());

    TransactionRecord.Staff staff = mapStaff(transactionRecordFirebaseObject.getStaff());

    String id =
        transactionRecordFirebaseObject.getId() != null
            ? transactionRecordFirebaseObject.getId()
            : "";

    int totalItems =
        transactionRecordFirebaseObject.getTotalItems() != null
            ? transactionRecordFirebaseObject.getTotalItems()
            : 0;

    double sellingPrices =
        transactionRecordFirebaseObject.getTotalSellingPrices() != null
            ? transactionRecordFirebaseObject.getTotalSellingPrices()
            : 0.0;

    double purchasePrices =
        transactionRecordFirebaseObject.getTotalPurchasePrices() != null
            ? transactionRecordFirebaseObject.getTotalPurchasePrices()
            : 0.0;

    double discountPrices =
        transactionRecordFirebaseObject.getTotalDiscountPrices() != null
            ? transactionRecordFirebaseObject.getTotalDiscountPrices()
            : 0.0;

    double finalPrices =
        transactionRecordFirebaseObject.getFinalPrices() != null
            ? transactionRecordFirebaseObject.getFinalPrices()
            : 0.0;

    String paymentMethod =
        transactionRecordFirebaseObject.getPaymentMethod() != null
            ? transactionRecordFirebaseObject.getPaymentMethod()
            : "";

    String note =
        transactionRecordFirebaseObject.getNote() != null
            ? transactionRecordFirebaseObject.getNote()
            : "";

    Date createdAt =
        transactionRecordFirebaseObject.getCreatedAt() != null
            ? transactionRecordFirebaseObject.getCreatedAt()
            : new Date();

    return new TransactionRecord(
        id,
        customer,
        staff,
        totalItems,
        sellingPrices,
        purchasePrices,
        discountPrices,
        finalPrices,
        paymentMethod,
        items,
        note,
        createdAt);
  }

  @NonNull @Contract("_ -> new")
  private static TransactionRecord.Customer mapCustomer(
      @Nullable TransactionRecordFirebaseObject.Customer c) {
    if (c == null) {
      return new TransactionRecord.Customer("", "", "", "");
    }
    return new TransactionRecord.Customer(
        c.getId() != null ? c.getId() : "",
        c.getName() != null ? c.getName() : "",
        c.getPhone() != null ? c.getPhone() : "",
        c.getEmail() != null ? c.getEmail() : "");
  }

  @NonNull @Contract("_ -> new")
  private static TransactionRecord.Staff mapStaff(
      @Nullable TransactionRecordFirebaseObject.Staff s) {
    if (s == null) {
      return new TransactionRecord.Staff("", "", "", "", "");
    }
    return new TransactionRecord.Staff(
        s.getId() != null ? s.getId() : "",
        s.getName() != null ? s.getName() : "",
        s.getEmail() != null ? s.getEmail() : "",
        s.getPhone() != null ? s.getPhone() : "",
        s.getRole() != null ? s.getRole() : "");
  }

  @NonNull private static List<TransactionRecord.Item> mapItems(
      @Nullable List<TransactionRecordFirebaseObject.Item> items) {
    if (items == null) return Collections.emptyList();
    return items.stream()
        .filter(i -> i.getId() != null && !i.getId().isEmpty())
        .map(TransactionRecordFirebaseObjectMapper::mapItem)
        .collect(Collectors.toList());
  }

  @NonNull @Contract("_ -> new")
  private static TransactionRecord.Item mapItem(
      @Nullable TransactionRecordFirebaseObject.Item item) {
    if (item == null)
      return new TransactionRecord.Item(
          "",
          "",
          "",
          0.0,
          0.0,
          0.0,
          0,
          "",
          new TransactionRecord.Item.Brand("", ""),
          new TransactionRecord.Item.Category("", ""));

    return new TransactionRecord.Item(
        item.getId() != null ? item.getId() : "",
        item.getName() != null ? item.getName() : "",
        item.getThumbnailUrl() != null ? item.getThumbnailUrl() : "",
        item.getSellingPrice() != null ? item.getSellingPrice() : 0.0,
        item.getDiscountPrice() != null ? item.getDiscountPrice() : 0.0,
        item.getPurchasePrice() != null ? item.getPurchasePrice() : 0.0,
        item.getQuantity() != null ? item.getQuantity() : 0,
        item.getBarcode() != null ? item.getBarcode() : "",
        mapBrand(item.getBrand()),
        mapCategory(item.getCategory()));
  }

  @NonNull @Contract("_ -> new")
  private static TransactionRecord.Item.Brand mapBrand(
      @Nullable TransactionRecordFirebaseObject.Item.Brand b) {
    if (b == null) return new TransactionRecord.Item.Brand("", "");
    return new TransactionRecord.Item.Brand(
        b.getId() != null ? b.getId() : "", b.getName() != null ? b.getName() : "");
  }

  @NonNull @Contract("_ -> new")
  private static TransactionRecord.Item.Category mapCategory(
      @Nullable TransactionRecordFirebaseObject.Item.Category c) {
    if (c == null) return new TransactionRecord.Item.Category("", "");
    return new TransactionRecord.Item.Category(
        c.getId() != null ? c.getId() : "", c.getName() != null ? c.getName() : "");
  }
}
