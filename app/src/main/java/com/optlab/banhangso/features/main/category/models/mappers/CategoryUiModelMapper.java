package com.optlab.banhangso.features.main.category.models.mappers;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import com.optlab.banhangso.models.domain.Category;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CategoryUiModelMapper {

  @NonNull public static CategoryUiModel fromDomain(@NonNull Category category) {
    CategoryUiModel categoryUiModel = new CategoryUiModel();
    categoryUiModel.setId(category.getId());
    categoryUiModel.setName(category.getName());
    categoryUiModel.setProductCount(category.getProductCount());
    categoryUiModel.setCreatedAt(category.getCreatedAt());
    categoryUiModel.setUpdatedAt(category.getUpdatedAt());
    return categoryUiModel;
  }

  @NonNull public static Category toDomain(@NonNull CategoryUiModel categoryUiModel) {
    Category category = new Category();
    category.setId(categoryUiModel.getId());
    category.setName(categoryUiModel.getName());
    category.setProductCount(categoryUiModel.getProductCount());
    category.setCreatedAt(categoryUiModel.getCreatedAt());
    category.setUpdatedAt(categoryUiModel.getUpdatedAt());
    return category;
  }
}
