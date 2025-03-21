package com.optlab.banhangso.factory;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.ui.category.viewmodel.CategorySelectionViewModel;

public class CategorySelectionViewModelFactory implements ViewModelProvider.Factory {

    private final CategoryRepository repository;

    public CategorySelectionViewModelFactory(CategoryRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategorySelectionViewModel.class)) {
            return (T) new CategorySelectionViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
