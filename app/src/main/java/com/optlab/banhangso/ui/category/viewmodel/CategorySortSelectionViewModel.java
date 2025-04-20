package com.optlab.banhangso.ui.category.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.CategorySortSelection;
import com.optlab.banhangso.util.UserPreferenceManager;

import dagger.hilt.android.lifecycle.HiltViewModel;

import timber.log.Timber;

import java.util.List;

import javax.inject.Inject;

/**
 * @noinspection rawtypes, unchecked
 */
@HiltViewModel
public class CategorySortSelectionViewModel extends ViewModel {
    private final SortOptionRepository repository;
    private final UserPreferenceManager userPreferenceManager;
    private final MutableLiveData<List<SortOption<Category.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public CategorySortSelectionViewModel(
            @NonNull @CategorySortSelection SortOptionRepository repository,
            @NonNull UserPreferenceManager userPreferenceManager) {
        this.repository = repository;
        this.userPreferenceManager = userPreferenceManager;

        // Set the sort options based on the repository
        sortOptions.setValue(repository.getSortOptions());

        // Set the sort option index based on the user's preference
        sortOptionIndex.setValue(
                repository.getPosition(
                        userPreferenceManager.getSortOption(
                                UserPreferenceManager.KEY_CATEGORY_SORT_OPTION)));
    }

    public MutableLiveData<List<SortOption<Category.SortField>>> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptionIndex(SortOption<Category.SortField> sortOption) {
        userPreferenceManager.setSortOption(
                sortOption, UserPreferenceManager.KEY_CATEGORY_SORT_OPTION);
        sortOptionIndex.setValue(repository.getPosition(sortOption));
        Timber.d("position: %s", repository.getPosition(sortOption));
    }

    public MutableLiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
