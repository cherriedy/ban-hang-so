package com.optlab.banhangso.ui.brand.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.util.SortFieldUtils;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.inject.Inject;

/**
 * @noinspection rawtypes
 */
@HiltViewModel
public class BrandListViewModel extends ViewModel {
    private final BrandRepository repository;
    private final MutableLiveData<List<Brand>> brands = new MutableLiveData<>();
    private final MediatorLiveData<List<Brand>> brandMediator = new MediatorLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<SortOption<Brand.SortField>> sortOption = new MutableLiveData<>();

    @Inject
    public BrandListViewModel(@Nonnull BrandRepository repository) {
        this.repository = repository;
        retrieveBrands();

        brandMediator.addSource(brands, unused -> updateBrands());
        brandMediator.addSource(searchQuery, unused -> updateBrands());
        brandMediator.addSource(sortOption, unused -> updateBrands());
    }

    private void updateBrands() {
        List<Brand> updatedList = brands.getValue();
        if (updatedList == null) {
            brandMediator.setValue(Collections.emptyList());
            return;
        }

        String query = searchQuery.getValue();
        if (!TextUtils.isEmpty(query)) {
            updatedList = filterByQuery(updatedList, query);
        }

        SortOption<Brand.SortField> selectedSortOption = sortOption.getValue();
        if (selectedSortOption != null) {
            updatedList.sort(
                    SortFieldUtils.getComparator(
                            selectedSortOption.getSortField(), selectedSortOption.isAscending()));
        }

        brandMediator.setValue(updatedList);
    }

    private List<Brand> filterByQuery(List<Brand> updatedList, String query) {
        return updatedList.stream()
                .filter(brand -> containQuery(brand.getName(), query))
                .collect(Collectors.toList());
    }

    private boolean containQuery(@NonNull String name, @NonNull String query) {
        return name.toLowerCase().contains(query.toLowerCase());
    }

    private void retrieveBrands() {
        repository.getBrands().observeForever(brands::setValue);
    }

    @Override
    protected void onCleared() {
        repository.getBrands().removeObserver(brands::setValue);
        super.onCleared();
    }

    public MutableLiveData<List<Brand>> getBrands() {
        return brandMediator;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public void setSortOption(SortOption<Brand.SortField> sortOption) {
        this.sortOption.setValue(sortOption);
    }
}
