package com.optlab.banhangso.data.repository.impl;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.repository.CategoryRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Singleton;

import timber.log.Timber;

@Singleton
public class CategoryRepositoryImpl implements CategoryRepository {
    private final FirebaseFirestore firestore;
    private final MutableLiveData<List<Category>> categories = new MutableLiveData<>();

    public CategoryRepositoryImpl(FirebaseFirestore firestore) {
        this.firestore = firestore;
        retrieveData();
    }

    /**
     * Converts a DocumentSnapshot to a Category object.
     *
     * @param doc The DocumentSnapshot to convert.
     * @return The converted Category object, or null if the conversion fails.
     */
    private Category docToCategory(DocumentSnapshot doc) {
        Category category = doc.toObject(Category.class);
        if (category == null) {
            Timber.e("Category is null for document: %s", doc.getId());
            return null;
        } else {
            Timber.d("Document %s is not null", doc.getId());
            category.setId(doc.getId());
            return category;
        }
    }

    private void retrieveData() {
        firestore.collection("categories")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null) {
                        Timber.e("Error getting documents: %s", error.getMessage());
                        return;
                    }

                    if (snapshots != null && !snapshots.isEmpty()) {
                        List<Category> categoryList = snapshots.getDocuments().stream()
                                .map(this::docToCategory)
                                .filter(Objects::nonNull) // Filter out null objects, if any
                                .collect(Collectors.toList());
                        categories.setValue(categoryList);
                    }
                });
    }

    @Override
    public LiveData<List<Category>> getCategories() {
        return categories;
    }

    @Override
    public Category getCategoryById(String id) {
        return Optional.ofNullable(categories.getValue())
                .flatMap(cl -> cl.stream()
                        .filter(category -> category.getId().equals(id))
                        .findFirst()
                )
                .map(this::safeClone)
                .orElse(null);
    }

    @Override
    public Category getCategoryByPosition(int position) {
        return Optional.ofNullable(categories.getValue())
                .flatMap(categories -> Optional.of(categories.get(position)))
                .map(this::safeClone)
                .orElse(null);
    }

    @Override
    public int getPositionById(String id) {
        List<Category> categoryList = categories.getValue();
        if (categoryList == null) return -1;
        return IntStream.range(0, categoryList.size())
                .filter(i -> categoryList.get(i).getId().equals(id))
                .findFirst()
                .orElse(-1);
    }

    private Category safeClone(Category category) {
        try {
            return (Category) category.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Cannot clone category: " + e);
        }
    }
}