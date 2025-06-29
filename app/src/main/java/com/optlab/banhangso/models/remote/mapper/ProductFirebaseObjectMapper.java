package com.optlab.banhangso.models.remote.mapper;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import java.util.List;
import java.util.stream.Collectors;

public class ProductFirebaseObjectMapper {

  private ProductFirebaseObjectMapper() {}

  @NonNull public static Product toDomain(@NonNull ProductFirebaseObject productFirebaseObject) {
    Product product = new Product();
    product.setId(productFirebaseObject.getId());
    product.setBarcode(productFirebaseObject.getBarcode());
    product.setCategory(productFirebaseObject.getCategory());
    product.setBrand(productFirebaseObject.getBrand());
    product.setName(productFirebaseObject.getName());
    product.setPurchasePrice(productFirebaseObject.getPurchasePrice());
    product.setSellingPrice(productFirebaseObject.getSellingPrice());
    product.setDiscountPrice(productFirebaseObject.getDiscountPrice());
    product.setAvatarUrl(productFirebaseObject.getAvatarUrl());
    product.setStockQuantity(productFirebaseObject.getStockQuantity());
    product.setDescription(productFirebaseObject.getDescription());
    product.setNote(productFirebaseObject.getNote());
    product.setStatus(productFirebaseObject.isStatus());
    product.setCreatedAt(productFirebaseObject.getCreatedAt());
    product.setUpdatedAt(productFirebaseObject.getUpdatedAt());
    return product;
  }

  @NonNull public static List<Product> toDomains(
      @NonNull List<ProductFirebaseObject> productFirebaseObjects) {
    return productFirebaseObjects.stream()
        .map(ProductFirebaseObjectMapper::toDomain)
        .collect(Collectors.toList());
  }

  @NonNull public static ProductFirebaseObject fromDomain(@NonNull Product product) {
    ProductFirebaseObject productFirebaseObject = new ProductFirebaseObject();
    productFirebaseObject.setId(product.getId());
    productFirebaseObject.setBarcode(product.getBarcode());
    productFirebaseObject.setCategory(product.getCategory());
    productFirebaseObject.setBrand(product.getBrand());
    productFirebaseObject.setName(product.getName());
    productFirebaseObject.setPurchasePrice(product.getPurchasePrice());
    productFirebaseObject.setSellingPrice(product.getSellingPrice());
    productFirebaseObject.setDiscountPrice(product.getDiscountPrice());
    productFirebaseObject.setAvatarUrl(product.getAvatarUrl());
    productFirebaseObject.setStockQuantity(product.getStockQuantity());
    productFirebaseObject.setDescription(product.getDescription());
    productFirebaseObject.setNote(product.getNote());
    productFirebaseObject.setStatus(product.isStatus());
    productFirebaseObject.setCreatedAt(product.getCreatedAt());
    productFirebaseObject.setUpdatedAt(product.getUpdatedAt());
    return productFirebaseObject;
  }
}
