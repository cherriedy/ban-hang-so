package com.optlab.banhangso.ui.brand.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.data.repository.impl.BrandRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class BrandSelectionViewModel extends ViewModel {
  private final BrandRepository repository;
  private final MutableLiveData<List<Brand>> brandsSourceLiveData = new MutableLiveData<>();
  private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>("");
  private final MediatorLiveData<List<Brand>> brandsLiveData = new MediatorLiveData<>();

  @Inject
  public BrandSelectionViewModel(@NonNull BrandRepository repository) {
    this.repository = repository;
    // Observe the brands change from repository and update the list.
    repository.getBrands().observeForever(brandsSourceLiveData::setValue);

    // Set up the mediator to observe changes in the brands list and search query.
    brandsLiveData.addSource(brandsSourceLiveData, unused -> updateBrands());
    brandsLiveData.addSource(searchQueryLiveData, unused -> updateBrands());
  }

  /** Update the brands list based on the search query. If the query is empty, show all brands. */
  private void updateBrands() {
    List<Brand> updatedBrands = brandsSourceLiveData.getValue();
    if (updatedBrands == null || updatedBrands.isEmpty()) return;

    String query = searchQueryLiveData.getValue();
    if (!TextUtils.isEmpty(query)) {
      updatedBrands =
          updatedBrands.stream()
              .filter(brand -> brand.getName().toLowerCase().contains(query.toLowerCase()))
              .collect(Collectors.toList());
    }

    brandsLiveData.setValue(updatedBrands);
  }

  @Override
  protected void onCleared() {
    repository.getBrands().removeObserver(brandsSourceLiveData::setValue);
    brandsLiveData.removeSource(brandsSourceLiveData);
    brandsLiveData.removeSource(searchQueryLiveData);
    super.onCleared();
  }

  public LiveData<List<Brand>> getBrands() {
    return brandsLiveData;
  }

  public void setSearchQuery(@Nullable String query) {
    searchQueryLiveData.setValue(query);
  }
}
