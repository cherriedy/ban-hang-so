package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.remote.BrandFirebaseObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BrandFirebaseObjectMapper {

  @NonNull public static Brand toDomain(@NonNull BrandFirebaseObject brandFirebaseObject) {
    Brand brand = new Brand();
    brand.setId(brandFirebaseObject.getId());
    brand.setStoreId(brandFirebaseObject.getStoreId());
    brand.setName(brandFirebaseObject.getName());
    brand.setProductCount(brandFirebaseObject.getProductCount());
    brand.setCreatedAt(brandFirebaseObject.getCreatedAt());
    brand.setUpdatedAt(brandFirebaseObject.getUpdatedAt());
    return brand;
  }

  @NonNull public static BrandFirebaseObject fromDomain(@NonNull Brand brand) {
    BrandFirebaseObject brandFirebaseObject = new BrandFirebaseObject();
    brandFirebaseObject.setId(brand.getId());
    brandFirebaseObject.setStoreId(brand.getStoreId());
    brandFirebaseObject.setName(brand.getName());
    brandFirebaseObject.setProductCount(brand.getProductCount());
    brandFirebaseObject.setCreatedAt(brand.getCreatedAt());
    brandFirebaseObject.setUpdatedAt(brand.getUpdatedAt());
    return brandFirebaseObject;
  }
}
