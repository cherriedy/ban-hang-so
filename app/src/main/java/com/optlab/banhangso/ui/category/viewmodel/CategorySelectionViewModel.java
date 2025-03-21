package com.optlab.banhangso.ui.category.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;

import java.util.List;

public class CategorySelectionViewModel extends ViewModel {

    private final CategoryRepository repository;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();
    private final MutableLiveData<Integer> checkedPosition = new MutableLiveData<>();

    public CategorySelectionViewModel(@NonNull CategoryRepository repository) {
        this.repository = repository;

        retrieveCategories();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.getCategories().removeObserver(categories::setValue);
    }

    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    public LiveData<Integer> getCheckedPosition() {
        return checkedPosition;
    }

    public void setCheckedPosition(int position) {
        checkedPosition.setValue(position);
    }

    private void retrieveCategories() {
        repository.getCategories().observeForever(categories::setValue);
    }

}
