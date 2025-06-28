package com.optlab.banhangso.features.main.product.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import javax.inject.Inject;

@HiltViewModel
public class ProductEditSharedViewModel extends ViewModel {
    private final MutableLiveData<Integer> selectedBrandPosition = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedCategoryPosition = new MutableLiveData<>();

    @Inject
    public ProductEditSharedViewModel() {}

    public LiveData<Integer> getSelectedBrandPosition() {
        return selectedBrandPosition;
    }

    public void setSelectBrandPosition(Integer brandPosition) {
        selectedBrandPosition.setValue(brandPosition);
    }

    public LiveData<Integer> getSelectedCategoryPosition() {
        return selectedCategoryPosition;
    }

    public void setSelectCategoryPosition(Integer categoryPosition) {
        selectedCategoryPosition.setValue(categoryPosition);
    }
}
