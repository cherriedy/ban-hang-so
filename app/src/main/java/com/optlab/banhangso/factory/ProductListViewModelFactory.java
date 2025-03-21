package com.optlab.banhangso.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.ui.product.viewmodel.ProductListViewModel;

public class ProductListViewModelFactory implements ViewModelProvider.Factory {

    private final ProductRepository repository;

    public ProductListViewModelFactory(ProductRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductListViewModel.class)) {
            return (T) new ProductListViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
