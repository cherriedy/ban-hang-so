package com.optlab.banhangso.features.main.product.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.brand.models.mappers.BrandUiModelMapper;
import com.optlab.banhangso.features.main.category.models.mappers.CategoryUiModelMapper;
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
        .category(
            product.getCategory() != null
                ? CategoryUiModelMapper.fromDomain(product.getCategory())
                : null)
        .brand(
            product.getBrand() != null ? BrandUiModelMapper.fromDomain(product.getBrand()) : null)
        .name(product.getName())
        .purchasePrice(product.getPurchasePrice())
        .sellingPrice(product.getSellingPrice())
        .thumbnailUrl(product.getThumbnailUrl())
        .imageUrls(product.getImageUrls())
        .stockQuantity(product.getStockQuantity())
        .description(product.getDescription())
        .status(product.getStatus())
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
    return new Product(
        productUiModel.getId(),
        null, // storeId - not available in UI model
        productUiModel.getBarcode(),
        productUiModel.getCategory() != null
            ? CategoryUiModelMapper.toDomain(productUiModel.getCategory())
            : null,
        productUiModel.getBrand() != null
            ? BrandUiModelMapper.toDomain(productUiModel.getBrand())
            : null,
        productUiModel.getName(),
        productUiModel.getPurchasePrice(),
        productUiModel.getSellingPrice(),
        productUiModel.getThumbnailUrl(),
        productUiModel.getImageUrls(),
        productUiModel.getStockQuantity(),
        productUiModel.getDescription(),
        productUiModel.getStatus(),
        productUiModel.getDiscountPrice(),
        productUiModel.getNote(),
        productUiModel.getCreatedAt(),
        productUiModel.getUpdatedAt());
  }
}
