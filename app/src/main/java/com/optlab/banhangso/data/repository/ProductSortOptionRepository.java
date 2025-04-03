package com.optlab.banhangso.data.repository;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import java.util.List;

public interface ProductSortOptionRepository {
  List<SortOption<Product.SortField>> getSortOptions();

  int getPosition(SortOption<Product.SortField> sortOption);

  SortOption<Product.SortField> getSortOption(int position);
}
