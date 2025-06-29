package com.optlab.banhangso.features.main.product.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.SortOptionRepository;
import com.optlab.banhangso.repositories.interfaces.preferences.AppPreferences;
import com.optlab.banhangso.repositories.sortoption.qualifier.ProductSortSelection;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import java.util.List;
import javax.inject.Inject;

/**
 * @noinspection unchecked, rawtypes
 */
@HiltViewModel
public class ProductSortSelectionViewModel extends ViewModel {
  private final SortOptionRepository repository;
  private final PreferencesRepository preferenceRepository;
  private final MutableLiveData<List<SortOption<Product.SortField>>> sortOptions =
      new MutableLiveData<>();
  private final MutableLiveData<Integer> sortOptionIndex = new MutableLiveData<>();
  private final CompositeDisposable compositeDisposable = new CompositeDisposable();

  @Inject
  public ProductSortSelectionViewModel(
      @ProductSortSelection SortOptionRepository repository,
      PreferencesRepository preferenceRepository) {
    this.repository = repository;
    this.preferenceRepository = preferenceRepository;

    sortOptions.setValue(repository.getSortOptions());

    // Handle reactive getSortOption call
    compositeDisposable.add(
        preferenceRepository
            .getSortOption(AppPreferences.KEY_PRODUCT_SORT_OPTION)
            .subscribe(
                sortOption -> sortOptionIndex.setValue(repository.getPosition(sortOption)),
                throwable -> sortOptionIndex.setValue(0) // Default to first option if not found
                ));
  }

  public List<SortOption<Product.SortField>> getSortOptions() {
    return sortOptions.getValue();
  }

  public void setSortOptionIndex(SortOption<Product.SortField> sortOption) {
    compositeDisposable.add(
        preferenceRepository
            .setSortOption(sortOption, AppPreferences.KEY_PRODUCT_SORT_OPTION)
            .subscribe());
    sortOptionIndex.setValue(repository.getPosition(sortOption));
  }

  public LiveData<Integer> getSortOptionIndex() {
    return sortOptionIndex;
  }

  @Override
  protected void onCleared() {
    super.onCleared();
    compositeDisposable.clear();
  }
}
