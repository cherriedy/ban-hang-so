package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.util.SortFieldUtils;

import dagger.hilt.android.lifecycle.HiltViewModel;

import timber.log.Timber;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@HiltViewModel
public class ProductListViewModel extends ViewModel {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MediatorLiveData<List<Product>> products = new MediatorLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> sortOption =
            new MutableLiveData<>();
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Category> selectedCategory = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();

    @Inject
    public ProductListViewModel(
            @NonNull ProductRepository productRepository,
            @NonNull CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        // Observe the categories change and update the list.
        categoryRepository.getCategories().observeForever(this::observeCategories);
        setupProductListObserver();
    }

    /**
     * Sets up the observers for the product list, sort option, selected category, and search query.
     */
    private void setupProductListObserver() {
        products.addSource(productRepository.getProducts(), unused -> updateProducts());
        products.addSource(sortOption, unused -> updateProducts());
        products.addSource(selectedCategory, unused -> updateProducts());
        products.addSource(searchQuery, unused -> updateProducts());
    }

    @Override
    protected void onCleared() {
        categoryRepository.getCategories().removeObserver(this::observeCategories);
        products.removeSource(productRepository.getProducts());
        products.removeSource(sortOption);
        products.removeSource(selectedCategory);
        products.removeSource(searchQuery);
        super.onCleared();
    }

    private void observeCategories(List<Category> categories) {
        this.categories.setValue(categories);
        updateProducts();
    }

    private void updateProducts() {
        Timber.d("updateProducts method called");
        List<Product> updatedList = productRepository.getProducts().getValue();
        if (updatedList == null) {
            products.setValue(Collections.emptyList());
            return;
        }

        // Filter by category if a category is selected.
        Category selectedCategory = this.selectedCategory.getValue();
        updatedList = filterByCategory(updatedList, selectedCategory);

        // Filter by search query if available.
        String query = searchQuery.getValue();
        if (query != null && !query.trim().isEmpty()) {
            updatedList = filterByQuery(updatedList, query);
        }

        // Sort the list if sort option is set.
        SortOption<Product.SortField> selectedSortOption = sortOption.getValue();
        if (selectedSortOption != null) {
            updatedList.sort(
                    SortFieldUtils.getComparator(
                            selectedSortOption.getSortField(), selectedSortOption.isAscending()));
        }

        products.setValue(updatedList);
    }

    @NonNull
    private static List<Product> filterByQuery(List<Product> updatedList, String query) {
        String lowercaseQuery = query.toLowerCase();
        updatedList =
                updatedList.stream()
                        .filter(product -> product.getName().toLowerCase().contains(lowercaseQuery))
                        .collect(Collectors.toList());
        return updatedList;
    }

    private static List<Product> filterByCategory(
            List<Product> updatedList, Category selectedCategory) {
        updatedList =
                selectedCategory == null
                        ? updatedList
                        : updatedList.stream()
                                .filter(product -> product.getCategory().equals(selectedCategory))
                                .collect(Collectors.toList());
        return updatedList;
    }

    public LiveData<List<Product>> getProducts() {
        return products;
    }

    public void setSortOption(SortOption<Product.SortField> selectedSortOption) {
        sortOption.setValue(selectedSortOption);
    }

    public void setCategory(int position) {
        if (position == RecyclerView.NO_POSITION) {
            selectedCategory.setValue(null);
        } else {
            selectedCategory.setValue(categoryRepository.getCategoryByPosition(position));
        }
    }

    public LiveData<Category> getSelectedCategory() {
        return selectedCategory;
    }

    public void setSearchQuery(@NonNull String query) {
        searchQuery.setValue(query);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }
}
