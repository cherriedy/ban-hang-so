package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.data.model.SortOption;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProductSortSelectionViewModel extends ViewModel {
    private final ProductSortOptionRepository repository;
    private final MutableLiveData<List<SortOption<Product.SortField>>> sortOptionsLiveData = new MutableLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> selectedSortOptionLiveData = new MutableLiveData<>();

    @Inject
    public ProductSortSelectionViewModel(@NonNull ProductSortOptionRepository repository) {
        this.repository = repository;
        sortOptionsLiveData.setValue(repository.getSortOptions());
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptionsLiveData.getValue();
    }

    public LiveData<SortOption<Product.SortField>> getSortOption() {
        return selectedSortOptionLiveData;
    }

    public void setSortOption(SortOption<Product.SortField> sortOption) {
        selectedSortOptionLiveData.setValue(sortOption);
    }
}