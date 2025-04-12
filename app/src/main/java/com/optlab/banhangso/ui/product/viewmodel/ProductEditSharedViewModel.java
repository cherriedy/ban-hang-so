package com.optlab.banhangso.ui.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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
