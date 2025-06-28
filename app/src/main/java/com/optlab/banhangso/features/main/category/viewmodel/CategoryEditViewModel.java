package com.optlab.banhangso.features.main.category.viewmodel;

import android.text.TextUtils;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.optlab.banhangso.features.main.category.state.CategoryEditValidationState;
import com.optlab.banhangso.internal.validators.CategoryValidator;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import java.util.function.Consumer;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class CategoryEditViewModel extends ViewModel {
    private final CategoryRepository repository;
    private final CategoryValidator validator;
    private final MutableLiveData<Category> category = new MutableLiveData<>();
    private final MutableLiveData<CategoryEditValidationState> validationState =
            new MutableLiveData<>(CategoryEditValidationState.empty());
    private final MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCreating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();

    @Inject
    public CategoryEditViewModel(
            @NonNull CategoryRepository repository, @NonNull CategoryValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public MutableLiveData<Category> getCategory() {
        return category;
    }

    public MutableLiveData<CategoryEditValidationState> getValidationState() {
        return validationState;
    }

    public MutableLiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public MutableLiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public MutableLiveData<Boolean> isCreating() {
        return isCreating;
    }

    public MutableLiveData<Boolean> getCreateResult() {
        return createResult;
    }

    /** Load a category by its ID. If the ID is empty, an empty category is set. */
    public void loadCategoryById(String id) {
        if (category.getValue() == null) {
            if (TextUtils.isEmpty(id)) {
                category.setValue(Category.empty());
            } else {
                category.setValue(repository.getCategoryById(id));
            }
        }
    }

    /** Update the validation state of the category. */
    private void updateValidationState(Consumer<CategoryEditValidationState> action) {
        action.accept(validationState.getValue());
        validationState.setValue(validationState.getValue());
    }

    /** Validate the name of the category. */
    public void validateName(String name) {
        updateValidationState(
                state -> {
                    state.setNameError(validator.validateName(name));
                });
    }

    /**
     * Handle the click event of the update button.
     *
     * @noinspection unused
     */
    public void onUpdateButtonClick(@NonNull View view) {
        Category currentCategory = category.getValue();
        if (currentCategory != null) {
            isUpdating.setValue(true);
            repository.updateCategory(
                    currentCategory,
                    isSuccessful -> {
                        isUpdating.setValue(false);
                        updateResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Category is null when trying to update");
        }
    }

    /**
     * Handle the click event of the create button.
     *
     * @noinspection unused
     */
    public void onCreateButtonClick(@NonNull View view) {
        Category currentCategory = category.getValue();
        if (currentCategory != null) {
            isCreating.setValue(true);
            repository.createCategory(
                    currentCategory,
                    isSuccessful -> {
                        isCreating.setValue(false);
                        createResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Category is null when trying to create");
        }
    }
}
