package com.optlab.banhangso.ui.brand.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;

import java.util.List;

public class BrandSelectionViewModel extends ViewModel {

    private final BrandRepository repository;
    private final MutableLiveData<List<Brand>> brands = new MutableLiveData<>();
    private final MutableLiveData<Integer> checkedPosition = new MutableLiveData<>(RecyclerView.NO_POSITION);

    public BrandSelectionViewModel(BrandRepository repository) {
        this.repository = repository;
        retrieveBrands();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.getBrands().removeObserver(brands::setValue);
    }

    public LiveData<List<Brand>> getBrands() {
        return brands;
    }

    private void retrieveBrands() {
        repository.getBrands().observeForever(brands::setValue);
    }

    public LiveData<Integer> getCheckedPosition() {
        return checkedPosition;
    }

    public void setCheckedPosition(int position) {
        checkedPosition.setValue(position);
    }
}
