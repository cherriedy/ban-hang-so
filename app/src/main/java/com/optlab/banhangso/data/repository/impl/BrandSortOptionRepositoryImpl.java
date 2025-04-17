package com.optlab.banhangso.data.repository.impl;

import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.inject.Singleton;

@Singleton
public class BrandSortOptionRepositoryImpl implements SortOptionRepository<Brand.SortField> {
    private static final List<SortOption<Brand.SortField>> SORT_FIELDS =
            Arrays.asList(
                    new SortOption<>(Brand.SortField.NAME, false),
                    new SortOption<>(Brand.SortField.NAME, true));

    @Override
    public List<SortOption<Brand.SortField>> getSortOptions() {
        return SORT_FIELDS;
    }

    @Override
    public int getPosition(SortOption<Brand.SortField> sortOption) {
        return IntStream.range(0, SORT_FIELDS.size())
                .filter(i -> SORT_FIELDS.get(i).equals(sortOption))
                .findFirst()
                .orElse(RecyclerView.NO_POSITION);
    }

    @Override
    public SortOption<Brand.SortField> getSortOption(int position) {
        return SORT_FIELDS.get(position);
    }
}
