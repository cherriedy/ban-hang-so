package com.optlab.banhangso.ui.category.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.app.SortOption;
import com.optlab.banhangso.data.reference.UserPreferenceManager;
import com.optlab.banhangso.data.repository.PreferenceRepository;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.CategorySortSelection;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

/**
 * @noinspection rawtypes, unchecked
 */
@HiltViewModel
public class CategorySortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final PreferenceRepository preferenceRepository;
    private final MutableLiveData<List<SortOption<Category.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public CategorySortSelectionViewModel(
            @NonNull @CategorySortSelection SortOptionRepository repository,
            @NonNull PreferenceRepository preferenceRepository) {
        this.repository = repository;
        this.preferenceRepository = preferenceRepository;

        // Set the sort options based on the repository
        sortOptions.setValue(repository.getSortOptions());

        // Set the sort option index based on the user's preference
        sortOptionIndex.setValue(
                repository.getPosition(
                        preferenceRepository.getSortOption(UserPreferenceManager.KEY_CATEGORY_SORT_OPTION)));
    }

    public MutableLiveData<List<SortOption<Category.SortField>>> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptionIndex(SortOption<Category.SortField> sortOption) {
        preferenceRepository.setSortOption(sortOption, UserPreferenceManager.KEY_CATEGORY_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
        Timber.d("position: %s", repository.getPosition(sortOption));
    }

    public MutableLiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
