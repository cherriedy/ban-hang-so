package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.data.model.SortOption;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

@HiltViewModel
public class ProductListViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MediatorLiveData<List<Product>> listMediatorLiveData = new MediatorLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> sortOptionLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<Category>> listCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Category> selectedCategoryLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>();

    @Inject
    public ProductListViewModel(@NonNull ProductRepository productRepository,
                                @NonNull CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;

        // Observe the categories change and update the list.
        categoryRepository.getCategories().observeForever(this::observeCategories);

        listMediatorLiveData.addSource(productRepository.getProducts(), unused -> updateProducts());
        listMediatorLiveData.addSource(sortOptionLiveData, unused -> updateProducts());
        listMediatorLiveData.addSource(selectedCategoryLiveData, unused -> updateProducts());
        listMediatorLiveData.addSource(searchQueryLiveData, unused -> updateProducts());
    }

    @Override
    protected void onCleared() {
        categoryRepository.getCategories().removeObserver(this::observeCategories);
        listMediatorLiveData.removeSource(productRepository.getProducts());
        listMediatorLiveData.removeSource(sortOptionLiveData);
        listMediatorLiveData.removeSource(selectedCategoryLiveData);
        listMediatorLiveData.removeSource(searchQueryLiveData);

        super.onCleared();
    }

    private void observeCategories(List<Category> categories) {
        listCategoryLiveData.setValue(categories);
        updateProducts();
    }

    private void updateProducts() {
        List<Product> updatedList = productRepository.getProducts().getValue();
        if (updatedList == null) {
            Timber.e("Product list is null in updateProducts method");
            listMediatorLiveData.setValue(Collections.emptyList());
            return;
        } else {
            Timber.d("Product list size in updateProducts method is %d", updatedList.size());
        }

        // Filter by category if a category is selected.
        Category selectedCategory = selectedCategoryLiveData.getValue();
        Timber.d("Selected category: %s", selectedCategory);
        updatedList = filterByCategory(updatedList, selectedCategory);

        // Filter by search query if available.
        String query = searchQueryLiveData.getValue();
        if (query != null && !query.trim().isEmpty()) {
            Timber.d("Search query: %s", query);
            updatedList = filterByQuery(updatedList, query);
        }

        // Sort the list if sort option is set.
        SortOption<Product.SortField> sortOption = sortOptionLiveData.getValue();
        if (sortOption != null) {
            updatedList.sort(Product.getComparator(sortOption.getSortField(), sortOption.isAscending()));
        }

        updatedList.forEach(product -> Timber.d("Product: %s", product));
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
        selectedCategoryLiveData.setValue(category);
    }

    public LiveData<Category> getSelectedCategory() {
        return selectedCategoryLiveData;
    }

    public void setSearchQuery(@NonNull String query) {
        searchQueryLiveData.setValue(query);
    }

    public LiveData<List<Category>> getCategories() {
        return listCategoryLiveData;
    }
}
