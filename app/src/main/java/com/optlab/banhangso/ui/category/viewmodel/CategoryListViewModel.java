package com.optlab.banhangso.ui.category.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.util.SortFieldUtils;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@HiltViewModel
public class CategoryListViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MediatorLiveData<List<Category>> categoryMediator = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<SortOption<Category.SortField>> sortOption =
            new MutableLiveData<>();

    @Inject
    public CategoryListViewModel(@NonNull CategoryRepository repository) {
        this.repository = repository;
        retrieveCategories();

        categoryMediator.addSource(categories, unused -> updateCategories());
        categoryMediator.addSource(searchQuery, unused -> updateCategories());
        categoryMediator.addSource(sortOption, unused -> updateCategories());
    }

    public MutableLiveData<List<Category>> getCategories() {
        return categories;
    }

    private void updateCategories() {
        List<Category> updatedList = categories.getValue();
        if (updatedList == null) {
            categoryMediator.setValue(Collections.emptyList());
            return;
        }

        String query = searchQuery.getValue();
        if (!TextUtils.isEmpty(query)) {
            updatedList = filterByQuery(updatedList, query);
        }

        SortOption<Category.SortField> selectedSortOption = sortOption.getValue();
        if (selectedSortOption != null) {
            updatedList.sort(
                    SortFieldUtils.getComparator(
                            selectedSortOption.getSortField(), selectedSortOption.isAscending()));
        }

        categoryMediator.setValue(updatedList);
    }

    private List<Category> filterByQuery(List<Category> updatedList, String query) {
        return updatedList.stream()
                .filter(category -> containQuery(category.getName(), query))
                .collect(Collectors.toList());
    }

    private boolean containQuery(@NonNull String name, @NonNull String query) {
        return name.toLowerCase().contains(query.toLowerCase());
    }

    private void retrieveCategories() {
        repository.getCategories().observeForever(categories::setValue);
    }

    @Override
    protected void onCleared() {
        repository.getCategories().removeObserver(categories::setValue);
        super.onCleared();
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setSortOption(SortOption<Category.SortField> sortOption) {
        this.sortOption.setValue(sortOption);
    }
}
