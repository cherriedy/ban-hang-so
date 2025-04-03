package com.optlab.banhangso.ui.category.viewmodel;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;
import com.optlab.banhangso.data.repository.impl.CategoryRepositoryImpl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategorySelectionViewModel extends ViewModel {
  private final CategoryRepository repository;
  private final MutableLiveData<List<Category>> categoriesSourceLiveData = new MutableLiveData<>();
  private final MutableLiveData<String> searchQueryLiveData = new MutableLiveData<>("");
  private final MediatorLiveData<List<Category>> categoriesLiveData = new MediatorLiveData<>();

  @Inject
  public CategorySelectionViewModel(@NonNull CategoryRepository repository) {
    this.repository = repository;
    // Observe the brands change from repository and update the list.
    repository.getCategories().observeForever(categoriesSourceLiveData::setValue);

    // Set up the mediator to observe changes in the categories list and search query.
    categoriesLiveData.addSource(categoriesSourceLiveData, unused -> updateCategories());
    categoriesLiveData.addSource(searchQueryLiveData, unused -> updateCategories());
  }

  /**
   * Update the categories list based on the search query. If the query is empty, show all
   * categories.
   */
  private void updateCategories() {
    List<Category> updatedCategories = categoriesSourceLiveData.getValue();
    if (updatedCategories == null || updatedCategories.isEmpty()) return;

    String query = searchQueryLiveData.getValue();
    if (!TextUtils.isEmpty(query)) {
      updatedCategories =
          updatedCategories.stream()
              .filter(brand -> brand.getName().toLowerCase().contains(query.toLowerCase()))
              .collect(Collectors.toList());
    }

    categoriesLiveData.setValue(updatedCategories);
  }

  @Override
  protected void onCleared() {
    repository.getCategories().removeObserver(categoriesSourceLiveData::setValue);
    categoriesLiveData.removeSource(categoriesSourceLiveData);
    categoriesLiveData.removeSource(searchQueryLiveData);
    super.onCleared();
  }

  public LiveData<List<Category>> getCategoriesSourceLiveData() {
    return categoriesSourceLiveData;
  }

  public void setSearchQuery(@Nullable String query) {
    searchQueryLiveData.setValue(query);
  }
}
