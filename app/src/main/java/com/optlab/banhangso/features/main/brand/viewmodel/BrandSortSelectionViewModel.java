package com.optlab.banhangso.features.main.brand.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.repositories.interfaces.PreferenceRepository;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import com.optlab.banhangso.repositories.perferences.AppPreferencesImpl;
import com.optlab.banhangso.repositories.sortoption.qualifier.BrandSortSelection;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.List;
import javax.inject.Inject;

/**
 * @noinspection rawtypes, unchecked
 */
@HiltViewModel
public class BrandSortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final PreferenceRepository preferenceRepository;
    private final MutableLiveData<List<SortOption<Brand.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public BrandSortSelectionViewModel(
            @NonNull @BrandSortSelection SortOptionRepository repository,
            @NonNull PreferenceRepository preferenceRepository) {
        this.repository = repository;
        this.preferenceRepository = preferenceRepository;

        // Set the sort options based on the repository
        sortOptions.setValue(repository.getSortOptions());

        // Set the sort option index based on the user's preference
        sortOptionIndex.setValue(
                repository.getPosition(
                        preferenceRepository.getSortOption(
                                AppPreferencesImpl.KEY_BRAND_SORT_OPTION)));
    }

    public MutableLiveData<List<SortOption<Brand.SortField>>> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptionIndex(SortOption<Brand.SortField> sortOption) {
        preferenceRepository.setSortOption(sortOption, AppPreferencesImpl.KEY_BRAND_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
    }

    public MutableLiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
