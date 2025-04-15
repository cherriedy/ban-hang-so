package com.optlab.banhangso.ui.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import dagger.hilt.android.lifecycle.HiltViewModel;

import javax.inject.Inject;

@HiltViewModel
public class ProductTabHostSharedViewModel extends ViewModel {
    private final MutableLiveData<Boolean> isGridModeEnabled = new MutableLiveData<>();
    private final MutableLiveData<String> searchQuery = new MutableLiveData<>();
    private final MutableLiveData<SortOption<Product.SortField>> selectedSortOption =
            new MutableLiveData<>();

    @Inject
    public ProductTabHostSharedViewModel() {}

    public LiveData<Boolean> getGridModeEnabled() {
        return isGridModeEnabled;
    }

    public void setGridModeEnabled(boolean isGrid) {
        isGridModeEnabled.setValue(isGrid);
    }

    public boolean toggleLayout() {
        Boolean currentState = isGridModeEnabled.getValue();
        isGridModeEnabled.setValue(currentState == null || !currentState);
        return Boolean.TRUE.equals(currentState);
    }

    public LiveData<String> getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String query) {
        searchQuery.setValue(query);
    }

    public LiveData<SortOption<Product.SortField>> getSelectedSortOption() {
        return selectedSortOption;
    }

    public void setSelectedSortOption(SortOption<Product.SortField> sortOption) {
        selectedSortOption.setValue(sortOption);
    }
}
