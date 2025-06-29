package com.optlab.banhangso.repositories.interfaces;

import com.optlab.banhangso.models.application.SortOption;
import java.util.List;

public interface SortOptionRepository<T extends Enum<T>> {
  List<SortOption<T>> getSortOptions();

  int getPosition(SortOption<T> sortOption);

  SortOption<T> getSortOption(int position);
}
