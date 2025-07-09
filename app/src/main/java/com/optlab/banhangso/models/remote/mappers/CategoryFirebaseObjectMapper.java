package com.optlab.banhangso.models.remote.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.models.remote.CategoryFirebaseObject;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryFirebaseObjectMapper {

  @NonNull public static Category toDomain(@NonNull CategoryFirebaseObject categoryFirebaseObject) {
    Category category = new Category();
    category.setId(categoryFirebaseObject.getId());
    category.setStoreId(categoryFirebaseObject.getStoreId());
    category.setProductCount(categoryFirebaseObject.getProductCount());
    category.setName(categoryFirebaseObject.getName());
    category.setCreatedAt(categoryFirebaseObject.getCreatedAt());
    category.setUpdatedAt(categoryFirebaseObject.getUpdatedAt());
    return category;
  }

  @NonNull public static CategoryFirebaseObject fromDomain(@NonNull Category category) {
    CategoryFirebaseObject categoryFirebaseObject = new CategoryFirebaseObject();
    categoryFirebaseObject.setId(category.getId());
    categoryFirebaseObject.setStoreId(category.getStoreId());
    categoryFirebaseObject.setName(category.getName());
    categoryFirebaseObject.setProductCount(category.getProductCount());
    categoryFirebaseObject.setCreatedAt(category.getCreatedAt());
    categoryFirebaseObject.setUpdatedAt(category.getUpdatedAt());
    return categoryFirebaseObject;
  }
}
