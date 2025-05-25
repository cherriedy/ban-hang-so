package com.optlab.banhangso.domain.repository;

import androidx.lifecycle.LiveData;

import com.optlab.banhangso.domain.model.Category;

import java.util.List;
import java.util.function.Consumer;

public interface CategoryRepository {
    LiveData<List<Category>> getCategories();

    Category getCategoryById(String id);

    Category getCategoryByPosition(int position);

    int getPositionById(String id);

    void updateCategory(Category currentCategory, Consumer<Boolean> isSuccessful);

    void createCategory(Category currentCategory, Consumer<Boolean> isSuccessful);

    void deleteCategory(Category currentCategory, Consumer<Boolean> isSuccessful);
}
