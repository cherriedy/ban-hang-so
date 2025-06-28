package com.optlab.banhangso.features.main.product.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import com.optlab.banhangso.repositories.perferences.AppPreferencesImpl;
import com.optlab.banhangso.repositories.sortoption.qualifier.ProductSortSelection;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.List;
import javax.inject.Inject;

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
                        preferenceRepository.getSortOption(
                                AppPreferencesImpl.KEY_PRODUCT_SORT_OPTION)));
    }

    public List<SortOption<Product.SortField>> getSortOptions() {
        return sortOptions.getValue();
    }

    public void setSortOptionIndex(SortOption<Product.SortField> sortOption) {
        preferenceRepository.setSortOption(sortOption, AppPreferencesImpl.KEY_PRODUCT_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
    }

    public LiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
