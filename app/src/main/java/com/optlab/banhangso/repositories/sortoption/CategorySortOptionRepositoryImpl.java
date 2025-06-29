package com.optlab.banhangso.repositories.sortoption;

import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class CategorySortOptionRepositoryImpl implements SortOptionRepository<Category.SortField> {
  private static final List<SortOption<Category.SortField>> SORT_OPTIONS =
      List.of(
          new SortOption<>(Category.SortField.UPDATE_TIME, true),
          new SortOption<>(Category.SortField.UPDATE_TIME, false),
          new SortOption<>(Category.SortField.NAME, true),
          new SortOption<>(Category.SortField.NAME, false));

  @Override
  public List<SortOption<Category.SortField>> getSortOptions() {
    return SORT_OPTIONS;
  }

  @Override
  public int getPosition(SortOption<Category.SortField> sortOption) {
    return SORT_OPTIONS.indexOf(sortOption);
  }

  @Override
  public SortOption<Category.SortField> getSortOption(int position) {
    return SORT_OPTIONS.get(position);
  }
}
