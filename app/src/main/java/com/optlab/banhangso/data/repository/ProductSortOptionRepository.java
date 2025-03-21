package com.optlab.banhangso.data.repository;

import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class ProductSortOptionRepository {

    private static ProductSortOptionRepository instance;

    private final List<SortOption<Product.SortField>> sortOptions;

    private ProductSortOptionRepository() {

        sortOptions = Arrays.asList(
                new SortOption<>(Product.SortField.NAME, true),
                new SortOption<>(Product.SortField.NAME, false),
                new SortOption<>(Product.SortField.SELLING_PRICE, true),
                new SortOption<>(Product.SortField.SELLING_PRICE, false)
        );

    }

    public static synchronized ProductSortOptionRepository getInstance() {
        if (instance == null) {
            instance = new ProductSortOptionRepository();
        }
        return instance;
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptions;
    }

    public int getPosition(SortOption<Product.SortField> sortOption) {
        return IntStream.range(0, sortOptions.size())
                .filter(i -> sortOptions.get(i).equals(sortOption))
                .findFirst()
                .orElse(RecyclerView.NO_POSITION);
    }

    public SortOption<Product.SortField> getSortOption(int position) {
        return sortOptions.get(position);
    }
}
