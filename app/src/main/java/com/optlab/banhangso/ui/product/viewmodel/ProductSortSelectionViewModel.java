package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.ProductSortOptionRepository;
import com.optlab.banhangso.util.UserPreferenceManager;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;

import javax.inject.Inject;

@HiltViewModel
public class ProductSortSelectionViewModel extends ViewModel {
    private final ProductSortOptionRepository repository;
    private final UserPreferenceManager userPreferenceManager;
    private final MutableLiveData<List<SortOption<Product.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedSortOptionPosition = new MutableLiveData<>();

    @Inject
    public ProductSortSelectionViewModel(
            @NonNull ProductSortOptionRepository repository,
            @NonNull UserPreferenceManager userPreferenceManager) {
        this.repository = repository;
        this.userPreferenceManager = userPreferenceManager;

        sortOptions.setValue(repository.getSortOptions());

        selectedSortOptionPosition.setValue(
                repository.getPosition(userPreferenceManager.getSortOption()));
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptions.getValue();
    }

    public void setSelectedSortOptionPosition(SortOption<Product.SortField> sortOption) {
        userPreferenceManager.setSortOption(sortOption);
        selectedSortOptionPosition.setValue(repository.getPosition(sortOption));
    }

    public LiveData<Integer> getSelectedSortOptionPosition() {
        return selectedSortOptionPosition;
    }
}
