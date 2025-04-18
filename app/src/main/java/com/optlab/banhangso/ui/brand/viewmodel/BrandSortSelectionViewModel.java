package com.optlab.banhangso.ui.brand.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.BrandSortSelection;
import com.optlab.banhangso.util.UserPreferenceManager;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * @noinspection rawtypes, unchecked
 */
@HiltViewModel
public class BrandSortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final UserPreferenceManager userPreferenceManager;
    private final MutableLiveData<List<SortOption<Brand.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public BrandSortSelectionViewModel(
            @NonNull @BrandSortSelection SortOptionRepository repository,
            @NonNull UserPreferenceManager userPreferenceManager) {
        this.repository = repository;
        this.userPreferenceManager = userPreferenceManager;

        // Set the sort options based on the repository
        sortOptions.setValue(repository.getSortOptions());

        // Set the sort option index based on the user's preference
        sortOptionIndex.setValue(
                repository.getPosition(
                        userPreferenceManager.getSortOption(
                                UserPreferenceManager.KEY_BRAND_SORT_OPTION)));
    }

    public MutableLiveData<List<SortOption<Brand.SortField>>> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptionIndex(SortOption<Brand.SortField> sortOption) {
        userPreferenceManager.setSortOption(
                sortOption, UserPreferenceManager.KEY_BRAND_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
    }

    public MutableLiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
