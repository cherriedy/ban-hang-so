package com.optlab.banhangso.internal.utilities;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.models.domain.Product;
import java.util.Comparator;
import lombok.experimental.UtilityClass;

/**
 * @noinspection SwitchStatementWithTooFewBranches
 */
@UtilityClass
public final class SortingUtils {
  public static Comparator<Category> getComparator(
      @NonNull Category.SortField sortField, boolean isAscending) {
    switch (sortField) {
      case NAME -> {
        return isAscending
            ? Comparator.comparing(Category::getName)
            : Comparator.comparing(Category::getName).reversed();
      }
      case UPDATE_TIME -> {
        return isAscending
            ? Comparator.comparing(Category::getUpdatedAt)
            : Comparator.comparing(Category::getUpdatedAt).reversed();
      }
      default -> throw new IllegalArgumentException("Unsupported Sort Field");
    }
  }

  public static Comparator<Brand> getComparator(
      @NonNull Brand.SortField sortField, boolean isAscending) {
    switch (sortField) {
      case NAME -> {
        return isAscending
            ? Comparator.comparing(Brand::getName)
            : Comparator.comparing(Brand::getName).reversed();
      }
      default -> throw new IllegalArgumentException("Unsupported Sort Field");
    }
  }

  public static Comparator<Product> getComparator(
      @NonNull Product.SortField sortField, boolean isAscending) {
    switch (sortField) {
      case NAME -> {
        return isAscending
            ? Comparator.comparing(Product::getName)
            : Comparator.comparing(Product::getName).reversed();
      }
      case SELLING_PRICE -> {
        return isAscending
            ? Comparator.comparingDouble(Product::getSellingPrice)
            : Comparator.comparingDouble(Product::getSellingPrice).reversed();
      }
      default -> throw new IllegalArgumentException("Unsupported Sort Field");
    }
  }
}
