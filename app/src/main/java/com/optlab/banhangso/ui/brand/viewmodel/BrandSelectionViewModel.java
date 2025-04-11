package com.optlab.banhangso.ui.brand.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

@HiltViewModel
public class BrandSelectionViewModel extends ViewModel {
    private final BrandRepository repository;
    private final MutableLiveData<List<Brand>> brandsSource = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MediatorLiveData<List<Brand>> brands = new MediatorLiveData<>();

    @Inject
    public BrandSelectionViewModel(@NonNull BrandRepository repository) {
        this.repository = repository;
        // Observe the brands change from repository and update the list.
        repository.getBrands().observeForever(brandsSource::setValue);

        // Set up the mediator to observe changes in the brands list and search query.
        brands.addSource(brandsSource, unused -> updateBrands());
        brands.addSource(searchQuery, unused -> updateBrands());
    }

    /** Update the brands list based on the search query. If the query is empty, show all brands. */
    private void updateBrands() {
        List<Brand> updatedBrands = brandsSource.getValue();
        if (updatedBrands == null || updatedBrands.isEmpty()) return;

        String query = searchQuery.getValue();
        if (!TextUtils.isEmpty(query)) {
            updatedBrands =
                    updatedBrands.stream()
                            .filter(
                                    brand ->
                                            brand.getName()
                                                    .toLowerCase()
                                                    .contains(query.toLowerCase()))
                            .collect(Collectors.toList());
        }

        brands.setValue(updatedBrands);
    }

    @Override
    protected void onCleared() {
        repository.getBrands().removeObserver(brandsSource::setValue);
        brands.removeSource(brandsSource);
        brands.removeSource(searchQuery);
        super.onCleared();
    }

    public LiveData<List<Brand>> getBrands() {
        return brands;
    }

    public void setSearchQuery(@Nullable String query) {
        searchQuery.setValue(query);
    }
}
