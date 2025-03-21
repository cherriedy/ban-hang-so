package com.optlab.banhangso.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.ui.product.viewmodel.ProductSortSelectionViewModel;

public class ProductSortSelectionViewModelFactory implements ViewModelProvider.Factory {

    private final ProductSortOptionRepository repository;

    public ProductSortSelectionViewModelFactory(ProductSortOptionRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductSortSelectionViewModel.class)) {
            return (T) new ProductSortSelectionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
