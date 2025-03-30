package com.optlab.banhangso.data.repository;

import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ProductSortOptionRepository {
  private static final List<SortOption<Product.SortField>> SORT_OPTIONS =
      List.of(
          new SortOption<>(Product.SortField.NAME, true),
          new SortOption<>(Product.SortField.NAME, false),
          new SortOption<>(Product.SortField.SELLING_PRICE, true),
          new SortOption<>(Product.SortField.SELLING_PRICE, false));

  @Inject
  public ProductSortOptionRepository() {}

  /** Get the list of sort options. */
  public List<SortOption<Product.SortField>> getSortOptions() {
    return SORT_OPTIONS;
  }

  /**
   * Get the position of the sort option in the list.
   *
   * @param sortOption the sort option to find the position
   * @return the position of the sort option in the list, or RecyclerView.NO_POSITION if not found
   */
  public int getPosition(SortOption<Product.SortField> sortOption) {
    return IntStream.range(0, SORT_OPTIONS.size())
        .filter(i -> SORT_OPTIONS.get(i).equals(sortOption))
        .findFirst()
        .orElse(RecyclerView.NO_POSITION);
  }

  /**
   * Get sort option by position in the list.
   *
   * @param position the position of the sort option in the list
   * @return the sort option at the specified position
   */
  public SortOption<Product.SortField> getSortOption(int position) {
    return SORT_OPTIONS.get(position);
  }
}
