package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;

import com.optlab.banhangso.data.model.Category;

import java.util.List;

public interface CategoryRepository {
    LiveData<List<Category>> getCategories();

    Category getCategoryById(String id);

    Category getCategoryByPosition(int position);

    int getPositionById(String id);
}
