package com.optlab.banhangso.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.ui.brand.viewmodel.BrandSelectionViewModel;

public class BrandSelectionViewModelFactory implements ViewModelProvider.Factory {

    private final BrandRepository repository;

    public BrandSelectionViewModelFactory(BrandRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(BrandSelectionViewModel.class)) {
            return (T) new BrandSelectionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
