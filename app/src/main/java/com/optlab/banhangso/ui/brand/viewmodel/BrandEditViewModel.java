package com.optlab.banhangso.ui.brand.viewmodel;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.repository.BrandRepository;
import com.optlab.banhangso.ui.brand.state.BrandEditValidationState;
import com.optlab.banhangso.util.validator.BrandValidator;

import dagger.hilt.android.lifecycle.HiltViewModel;

import timber.log.Timber;

import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class BrandEditViewModel extends ViewModel {
    private final BrandRepository repository;
    private final BrandValidator validator;
    private final MutableLiveData<Brand> brand = new MutableLiveData<>();
    private final MutableLiveData<BrandEditValidationState> validationState =
            new MutableLiveData<>(BrandEditValidationState.empty());
    private final MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCreating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();

    @Inject
    public BrandEditViewModel(
            @NonNull BrandRepository repository, @NonNull BrandValidator validator) {
        this.repository = repository;
        this.validator = validator;
    }

    public void loadBrandById(@NonNull String id) {
        if (brand.getValue() == null) {
            if (TextUtils.isEmpty(id)) {
                brand.setValue(Brand.empty());
            } else {
                brand.setValue(repository.getBrandById(id));
            }
        }
    }

    public LiveData<Brand> getBrand() {
        return brand;
    }

    public LiveData<BrandEditValidationState> getValidateState() {
        return validationState;
    }

    private void updateValidationState(Consumer<BrandEditValidationState> action) {
        BrandEditValidationState state = validationState.getValue();
        if (state != null) {
            action.accept(state);
            validationState.setValue(state);
        } else {
            Timber.e("Validation state is null");
        }
    }

    public void validateName(@NonNull String name) {
        updateValidationState(
                state -> {
                    state.setNameError(validator.validateName(name));
                    Timber.d("Has error: %s", state.hasNoError());
                });
    }

    /**
     * @noinspection unused
     */
    public void onUpdateButtonClick(@NonNull View view) {
        Brand currentBrand = brand.getValue();
        if (currentBrand != null) {
            isUpdating.setValue(true);
            repository.updateBrand(
                    currentBrand,
                    isSuccessful -> {
                        isUpdating.setValue(false);
                        updateResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Brand is null when trying to update");
        }
    }

    /**
     * @noinspection unused
     */
    public void onCreateButtonClick(@NonNull View view) {
        Brand currentBrand = brand.getValue();
        if (currentBrand != null) {
            isCreating.setValue(true);
            repository.createBrand(
                    currentBrand,
                    isSuccessful -> {
                        isCreating.setValue(false);
                        createResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Brand is null when trying to create");
        }
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResult;
    }

    public LiveData<Boolean> isUpdating() {
        return isUpdating;
    }

    public LiveData<Boolean> isCreating() {
        return isCreating;
    }
}
