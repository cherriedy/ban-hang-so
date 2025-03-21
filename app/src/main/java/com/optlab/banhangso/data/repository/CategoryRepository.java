package com.optlab.banhangso.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.data.model.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class CategoryRepository {

    private static CategoryRepository instance;
    private MutableLiveData<List<Category>> categoryList = new MutableLiveData<>();

    private CategoryRepository() {
        List<Category> categories = new ArrayList<>();
        categories.add(new Category(1, "CG001", "Dầu ăn"));
        categories.add(new Category(2, "CG002", "Bánh bao, bánh mì, pizza"));
        categories.add(new Category(3, "CG003", "Mandu, há cảo, sủi cảo"));
        categories.add(new Category(4, "CG004", "Nước tăng lực, bù khoáng"));
        categories.add(new Category(5, "CG005", "Cháo gói, cháo tươi"));

        categoryList.setValue(categories);
    }

    public static synchronized CategoryRepository getInstance() {
        if (instance == null) {
            instance = new CategoryRepository();
        }
        return instance;
    }

    public LiveData<List<Category>> getCategories() {
        return categoryList;
    }

    public Category getCategoryById(int id) {
        return Optional.ofNullable(categoryList.getValue())
                .flatMap(categories -> getFirstMatch(categories, id))
                .map(this::safeClone)
                .orElse(null);
    }

    public Category getCategoryByPosition(int position) {
        return Optional.ofNullable(categoryList.getValue())
                .flatMap(categories -> Optional.of(categories.get(position)))
                .map(this::safeClone)
                .orElse(null);
    }

    public int getPositionById(int id) {
        List<Category> categories = categoryList.getValue();
        if (categories == null) return -1;
        return IntStream.range(0, categories.size())
                .filter(i -> categories.get(i).getId() == id)
                .findFirst()
                .orElse(-1);
    }

    private Optional<Category> getFirstMatch(List<Category> categories, int id) {
        return categories.stream()
                .filter(category -> category.getId() == id)
                .findFirst();

    }

    private Category safeClone(Category category) {
        try {
            return (Category) category.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}