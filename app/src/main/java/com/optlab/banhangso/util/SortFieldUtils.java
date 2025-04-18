package com.optlab.banhangso.util;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Product;

import java.util.Comparator;

/**
 * @noinspection SwitchStatementWithTooFewBranches
 */
public final class SortFieldUtils {
    public static Comparator<Brand> getComparator(Brand.SortField sortField, boolean isAscending) {
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
            Product.SortField sortField, boolean isAscending) {
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
