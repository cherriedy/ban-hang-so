package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.model.SortOption;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ProductListViewModel extends ViewModel {

    private final ProductRepository repository;
    private final MediatorLiveData<List<Product>> listMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> sortOptionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Category> categoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>();

    public ProductListViewModel(@NonNull ProductRepository repository) {
        this.repository = repository;

        listMediatorLiveData.addSource(repository.getProducts(), products -> updateProducts());
        listMediatorLiveData.addSource(sortOptionLiveData, option -> updateProducts());
        listMediatorLiveData.addSource(categoryLiveData, category -> updateProducts());
        listMediatorLiveData.addSource(searchQueryLiveData, query -> updateProducts());
    }

    private void updateProducts() {
        List<Product> updatedList = Objects.requireNonNullElse(
                repository.getProducts().getValue(),
                Collections.emptyList()
        );

        // Filter by category if a category is selected.
        Category selectedCategory = categoryLiveData.getValue();
        updatedList = filterByCategory(updatedList, selectedCategory);

        // Filter by search query if available.
        String query = searchQueryLiveData.getValue();
        if (query != null && !query.trim().isEmpty()) {
            updatedList = filterByQuery(updatedList, query);
        }

        // Sort the list if sort option is set.
        SortOption<Product.SortField> sortOption = Objects.requireNonNull(sortOptionLiveData.getValue());
        updatedList.sort(Product.getComparator(sortOption.getSortField(), sortOption.isAscending()));

        listMediatorLiveData.setValue(updatedList);
    }

    @NonNull
    private static List<Product> filterByQuery(List<Product> updatedList, String query) {
        String lowercaseQuery = query.toLowerCase();
        updatedList = updatedList.stream()
                .filter(product -> product.getName()
                        .toLowerCase()
                        .contains(lowercaseQuery)
                )
                .collect(Collectors.toList());
        return updatedList;
    }

    private static List<Product> filterByCategory(List<Product> updatedList, Category selectedCategory) {
        updatedList = selectedCategory == null ? updatedList
                : updatedList.stream()
                .filter(product -> product.getCategory().equals(selectedCategory))
                .collect(Collectors.toList());
        return updatedList;
    }

    public LiveData<List<Product>> getProducts() {
        return listMediatorLiveData;
    }

    public void setSortOption(SortOption<Product.SortField> sortOption) {
        sortOptionLiveData.setValue(sortOption);
    }

    public void setCategory(@Nullable Category category) {
        categoryLiveData.setValue(category);
    }

    public LiveData<Category> getCategory() {
        return categoryLiveData;
    }

    public void setSearchQuery(@NonNull String query) {
        searchQueryLiveData.setValue(query);
    }
}
