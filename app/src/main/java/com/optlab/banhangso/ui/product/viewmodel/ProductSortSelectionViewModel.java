package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.ProductSortSelection;
import com.optlab.banhangso.util.UserPreferenceManager;

import dagger.hilt.android.lifecycle.HiltViewModel;

import java.util.List;

import javax.inject.Inject;

/**
 * @noinspection unchecked, rawtypes
 */
@HiltViewModel
public class ProductSortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final UserPreferenceManager userPreferenceManager;
    private final MutableLiveData<List<SortOption<Product.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public ProductSortSelectionViewModel(
            @NonNull @ProductSortSelection SortOptionRepository repository,
            @NonNull UserPreferenceManager userPreferenceManager) {
        this.repository = repository;
        this.userPreferenceManager = userPreferenceManager;

        sortOptions.setValue(repository.getSortOptions());

        sortOptionIndex.setValue(
                repository.getPosition(
                        userPreferenceManager.getSortOption(
                                UserPreferenceManager.KEY_PRODUCT_SORT_OPTION)));
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptions.getValue();
    }

    public void setSortOptionIndex(SortOption<Product.SortField> sortOption) {
        userPreferenceManager.setSortOption(
                sortOption, UserPreferenceManager.KEY_PRODUCT_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
    }

    public LiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
