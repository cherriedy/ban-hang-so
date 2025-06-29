package com.optlab.banhangso.features.main.product.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.product.models.ProductUiModel;
import com.optlab.banhangso.models.domain.Product;
import java.util.List;
import java.util.stream.Collectors;

public class ProductUiModelMapper {

  private ProductUiModelMapper() {}

  @NonNull public static ProductUiModel fromDomain(@NonNull Product product) {
    return ProductUiModel.builder()
        .id(product.getId())
        .barcode(product.getBarcode())
        .category(product.getCategory())
        .brand(product.getBrand())
        .name(product.getName())
        .purchasePrice(product.getPurchasePrice())
        .sellingPrice(product.getSellingPrice())
        .avatarUrl(product.getAvatarUrl())
        .stockQuantity(product.getStockQuantity())
        .description(product.getDescription())
        .status(product.isStatus())
        .discountPrice(product.getDiscountPrice())
        .note(product.getNote())
        .createdAt(product.getCreatedAt())
        .updatedAt(product.getUpdatedAt())
        .build();
  }

  @NonNull public static List<Product> toDomains(@NonNull List<ProductUiModel> productUiModels) {
    return productUiModels.stream()
        .map(ProductUiModelMapper::toDomain)
        .collect(Collectors.toList());
  }

  @NonNull public static Product toDomain(@NonNull ProductUiModel productUiModel) {
    return Product.builder()
        .id(productUiModel.getId())
        .barcode(productUiModel.getBarcode())
        .category(productUiModel.getCategory())
        .brand(productUiModel.getBrand())
        .name(productUiModel.getName())
        .purchasePrice(productUiModel.getPurchasePrice())
        .sellingPrice(productUiModel.getSellingPrice())
        .avatarUrl(productUiModel.getAvatarUrl())
        .stockQuantity(productUiModel.getStockQuantity())
        .description(productUiModel.getDescription())
        .status(productUiModel.getStatus())
        .discountPrice(productUiModel.getDiscountPrice())
        .note(productUiModel.getNote())
        .createdAt(productUiModel.getCreatedAt())
        .updatedAt(productUiModel.getUpdatedAt())
        .build();
  }
}
