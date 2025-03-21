package com.optlab.banhangso.ui.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.data.model.SortOption;

import java.util.List;

public class ProductSortSelectionViewModel extends ViewModel {

    private final ProductSortOptionRepository repository;
    private final MutableLiveData<SortOption<Product.SortField>> sortOptionLiveData = new MutableLiveData<>();

    public ProductSortSelectionViewModel(ProductSortOptionRepository repository) {
        this.repository = repository;
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return repository.getSortOptions();
    }

    public LiveData<SortOption<Product.SortField>> getSortOption() {
        return sortOptionLiveData;
    }

    public void setSortOption(SortOption<Product.SortField> sortOption) {
        sortOptionLiveData.setValue(sortOption);
    }
}