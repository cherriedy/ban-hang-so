package com.optlab.banhangso.ui.category.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@HiltViewModel
public class CategorySelectionViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categoriesSource = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Category>> categories = new MediatorLiveData<>();

    @Inject
    public CategorySelectionViewModel(@NonNull CategoryRepository repository) {
        this.repository = repository;
        // Observe the brands change from repository and update the list.
        repository.getCategories().observeForever(categoriesSource::setValue);

        // Set up the mediator to observe changes in the categories list and search query.
        categories.addSource(categoriesSource, unused -> updateCategories());
        categories.addSource(searchQuery, unused -> updateCategories());
    }

    /**
     * Update the categories list based on the search query. If the query is empty, show all
     * categories.
     */
    private void updateCategories() {
        List<Category> updatedCategories = categoriesSource.getValue();
        if (updatedCategories == null || updatedCategories.isEmpty()) return;

        String query = searchQuery.getValue();
        if (!TextUtils.isEmpty(query)) {
            updatedCategories =
                    updatedCategories.stream()
                            .filter(
                                    brand ->
                                            brand.getName()
                                                    .toLowerCase()
                                                    .contains(query.toLowerCase()))
                            .collect(Collectors.toList());
        }

        categories.setValue(updatedCategories);
    }

    @Override
    protected void onCleared() {
        repository.getCategories().removeObserver(categoriesSource::setValue);
        categories.removeSource(categoriesSource);
        categories.removeSource(searchQuery);
        super.onCleared();
    }

    public LiveData<List<Category>> getCategoriesSource() {
        return categoriesSource;
    }

    public void setSearchQuery(@Nullable String query) {
        searchQuery.setValue(query);
    }
}
