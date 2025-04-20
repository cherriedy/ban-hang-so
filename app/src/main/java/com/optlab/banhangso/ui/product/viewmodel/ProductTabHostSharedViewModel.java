package com.optlab.banhangso.ui.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class ProductTabHostSharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isGridModeEnabled = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> productSortOption =
            new MutableLiveData<>();
    private final MutableLiveData<SortOption<Brand.SortField>> brandSortOption =
            new MutableLiveData<>();
    private final MutableLiveData<SortOption<Category.SortField>> categorySortOption =
            new MutableLiveData<>();

    @Inject
    public ProductTabHostSharedViewModel() {}

    public LiveData<Boolean> getGridModeEnabled() {
        return isGridModeEnabled;
    }

    public void setGridModeEnabled(boolean isGrid) {
        isGridModeEnabled.setValue(isGrid);
    }

    public boolean toggleProductLayout() {
        Boolean currentState = isGridModeEnabled.getValue();
        isGridModeEnabled.setValue(currentState == null || !currentState);
        return Boolean.TRUE.equals(currentState);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<SortOption<Product.SortField>> getProductSortOption() {
        return productSortOption;
    }

    public void setProductSortOption(SortOption<Product.SortField> sortOption) {
        productSortOption.setValue(sortOption);
    }

    public void setBrandSortOption(SortOption<Brand.SortField> sortOption) {
        brandSortOption.setValue(sortOption);
    }

    public LiveData<SortOption<Brand.SortField>> getBrandSortOption() {
        return brandSortOption;
    }

    public LiveData<SortOption<Category.SortField>> getCategorySortOption() {
        return categorySortOption;
    }

    public void setCategorySortOption(SortOption<Category.SortField> sortOption) {
        categorySortOption.setValue(sortOption);
    }
}
