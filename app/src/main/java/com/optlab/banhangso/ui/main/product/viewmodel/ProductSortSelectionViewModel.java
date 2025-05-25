package com.optlab.banhangso.ui.main.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.domain.model.Product;
import com.optlab.banhangso.domain.util.SortOption;
import com.optlab.banhangso.data.repository.references.UserPreferenceManager;
import com.optlab.banhangso.domain.repository.PreferenceRepository;
import com.optlab.banhangso.domain.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.ProductSortSelection;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * @noinspection unchecked, rawtypes
 */
@HiltViewModel
public class ProductSortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final PreferenceRepository preferenceRepository;
    private final MutableLiveData<List<SortOption<Product.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public ProductSortSelectionViewModel(
            @ProductSortSelection SortOptionRepository repository,
            PreferenceRepository preferenceRepository) {
        this.repository = repository;
        this.preferenceRepository = preferenceRepository;

        sortOptions.setValue(repository.getSortOptions());

        sortOptionIndex.setValue(
                repository.getPosition(
                        preferenceRepository.getSortOption(UserPreferenceManager.KEY_PRODUCT_SORT_OPTION)));
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptions.getValue();
    }

    public void setSortOptionIndex(SortOption<Product.SortField> sortOption) {
        preferenceRepository.setSortOption(sortOption, UserPreferenceManager.KEY_PRODUCT_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
    }

    public LiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
