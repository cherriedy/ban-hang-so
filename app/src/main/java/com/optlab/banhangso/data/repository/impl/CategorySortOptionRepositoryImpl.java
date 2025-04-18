package com.optlab.banhangso.data.repository.impl;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;

import java.util.Arrays;
import java.util.List;

import javax.inject.Singleton;

@Singleton
public class CategorySortOptionRepositoryImpl implements SortOptionRepository<Category.SortField> {
    private static final List<SortOption<Category.SortField>> SORT_OPTIONS =
            Arrays.asList(
                    new SortOption<>(Category.SortField.TIME, true),
                    new SortOption<>(Category.SortField.TIME, false),
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
