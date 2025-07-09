package com.optlab.banhangso.features.main.brand.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.models.domain.Brand;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BrandUiModelMapper {

  @NonNull public static BrandUiModel fromDomain(@NonNull Brand brand) {
    BrandUiModel brandUiModel = new BrandUiModel();
    brandUiModel.setId(brand.getId());
    brandUiModel.setName(brand.getName());
    brandUiModel.setProductCount(brand.getProductCount());
    brandUiModel.setCreatedAt(brand.getCreatedAt());
    brandUiModel.setUpdatedAt(brand.getUpdatedAt());
    return brandUiModel;
  }

  @NonNull public static Brand toDomain(@NonNull BrandUiModel brandUiModel) {
    Brand brand = new Brand();
    brand.setId(brandUiModel.getId());
    brand.setName(brandUiModel.getName());
    brand.setProductCount(brandUiModel.getProductCount());
    brand.setCreatedAt(brandUiModel.getCreatedAt());
    brand.setUpdatedAt(brandUiModel.getUpdatedAt());
    return brand;
  }
}
