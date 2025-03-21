package com.optlab.banhangso.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.ui.product.viewmodel.ProductEditViewModel;

public class ProductEditViewModelFactory extends AbstractSavedStateViewModelFactory {

    private final ProductRepository repository;

    public ProductEditViewModelFactory(@NonNull SavedStateRegistryOwner owner, ProductRepository repository) {
        super(owner, null);
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    protected <T extends ViewModel> T create(@NonNull String key, @NonNull Class<T> modelClass, @NonNull SavedStateHandle savedStateHandle) {
        if (modelClass.isAssignableFrom(ProductEditViewModel.class)) {
            return (T) new ProductEditViewModel(savedStateHandle, repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
