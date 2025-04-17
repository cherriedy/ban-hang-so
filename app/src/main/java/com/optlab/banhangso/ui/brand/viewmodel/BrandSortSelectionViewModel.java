package com.optlab.banhangso.ui.brand.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.data.repository.SortOptionRepository;
import com.optlab.banhangso.data.repository.qualifier.BrandSortSelection;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * @noinspection rawtypes, unchecked
 */
@HiltViewModel
public class BrandSortSelectionViewModel extends ViewModel {
    private final SortOptionRepository sortRepository;
    private final MutableLiveData<List<SortOption<Brand.SortField>>> sortOptions =
            new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();

    @Inject
    public BrandSortSelectionViewModel(
            @NonNull @BrandSortSelection SortOptionRepository sortRepository) {
        this.sortRepository = sortRepository;

        sortOptions.setValue(sortRepository.getSortOptions());
    }

    public MutableLiveData<List<SortOption<Brand.SortField>>> getSortOptions() {
        return sortOptions;
    }

    public void setSortOptionIndex(SortOption<Brand.SortField> sortOption) {
        sortOptionIndex.setValue(sortRepository.getPosition(sortOption));
    }

    public MutableLiveData<Integer> getSortOptionIndex() {
        return sortOptionIndex;
    }
}
