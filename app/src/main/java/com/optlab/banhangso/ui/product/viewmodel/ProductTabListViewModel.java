package com.optlab.banhangso.ui.product.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ProductTabListViewModel extends ViewModel {

    private final MutableLiveData<Boolean> toggleLayoutLiveData = new MediatorLiveData<>();

    public LiveData<Boolean> getToggleLayout() {
        return toggleLayoutLiveData;
    }

    public void setToggleLayout(boolean isGrid) {
        toggleLayoutLiveData.setValue(isGrid);
    }

    public void toggleLayout() {
        Boolean currentState = toggleLayoutLiveData.getValue();
        toggleLayoutLiveData.setValue(currentState == null || !currentState);
    }
}
