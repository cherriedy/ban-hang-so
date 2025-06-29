package com.optlab.banhangso.features.main.product.viewmodels;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import androidx.paging.PagingData;
import androidx.paging.rxjava3.PagingRx;
import com.optlab.banhangso.models.application.SortOption;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.repositories.interfaces.ProductRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.schedulers.Schedulers;
import javax.inject.Inject;
import kotlinx.coroutines.CoroutineScope;
import timber.log.Timber;

@HiltViewModel
public class ProductListViewModel extends ViewModel {
  private final ProductRepository productRepository;
  private final PreferencesRepository preferencesRepository;
  private final CompositeDisposable disposables = new CompositeDisposable();
  private final ObservableField<String> searchQuery = new ObservableField<>();
  private final MutableLiveData<SortOption<Product.SortField>> sortOption = new MutableLiveData<>();
  private final BehaviorProcessor<String> searchProcessor = BehaviorProcessor.createDefault("");
  private final CoroutineScope viewModelScope = ViewModelKt.getViewModelScope(this);
  private final LiveData<Boolean> layoutMode;

  private Flowable<PagingData<Product>> products;

  @Inject
  public ProductListViewModel(
      @NonNull ProductRepository productRepository, PreferencesRepository preferencesRepository) {
    this.productRepository = productRepository;
    this.preferencesRepository = preferencesRepository;

    layoutMode =
        LiveDataReactiveStreams.fromPublisher(
            preferencesRepository
                .observeLayoutMode()
                .subscribeOn(Schedulers.io())
                .toFlowable(BackpressureStrategy.LATEST));

    setUpProducts();
    setUpSearchQueryChanges();
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    super.onCleared();
  }

  private void setUpSearchQueryChanges() {
    searchQuery.addOnPropertyChangedCallback(
        new Observable.OnPropertyChangedCallback() {
          @SuppressWarnings("unchecked")
          @Override
          public void onPropertyChanged(Observable sender, int propertyId) {
            String query = ((ObservableField<String>) sender).get();
            searchProcessor.onNext(query != null ? query : "");
          }
        });

    searchQuery.set(""); // Initial display with all products
  }

  public ObservableField<String> getSearchQuery() {
    return searchQuery;
  }

  public Flowable<PagingData<Product>> getProducts() {
    return products;
  }

  public LiveData<Boolean> getLayoutMode() {
    return layoutMode;
  }

  private void setUpProducts() {
    // Create a Flowable that will emit new PagingData based on whether we're searching or not
    products =
        searchProcessor
            .distinctUntilChanged()
            .doOnNext(query -> Timber.d("Product search query: %s", query))
            .switchMap(
                query -> {
                  if (query.isBlank()) {
                    // When query is empty/blank, use the all products cache
                    return productRepository.getProducts();
                  } else {
                    // When searching, use the search-specific PagingSource
                    return productRepository.searchProduct(query);
                  }
                });

    // Cache the paging data in the coroutine scope
    PagingRx.cachedIn(products, viewModelScope);
  }

  public void setSortOption(SortOption<Product.SortField> sortOption) {
    this.sortOption.setValue(sortOption);
  }

  public void toggleLayout(@NonNull View view) {
    boolean current = layoutMode.getValue();

    disposables.add(
        preferencesRepository
            .setLayoutMode(!current)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                () -> Timber.d("Layout mode toggled successfully"),
                error -> Timber.e(error, "Error toggling layout mode")));
  }
}
