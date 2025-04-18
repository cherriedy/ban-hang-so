package com.optlab.banhangso.data.repository;

import com.optlab.banhangso.data.model.SortOption;

import java.util.List;

public interface SortOptionRepository<T extends Enum<T>> {
    List<SortOption<T>> getSortOptions();

    int getPosition(SortOption<T> sortOption);

    SortOption<T> getSortOption(int position);
}
