package com.optlab.banhangso.ui.product.viewmodel;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.ui.product.state.ProductEditValidationState;
import com.optlab.banhangso.util.validator.ProductValidator;

import dagger.hilt.android.lifecycle.HiltViewModel;

import timber.log.Timber;

import java.util.function.Consumer;

import javax.inject.Inject;

@HiltViewModel
public class ProductEditViewModel extends ViewModel {
    private final ProductRepository repository;
    private final ProductValidator validator;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<ProductEditValidationState> validationState =
            new MutableLiveData<>(ProductEditValidationState.empty());
    private final MediatorLiveData<Boolean> updateButtonState = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isUpdating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDeleting = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCreating = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();

    @Inject
    public ProductEditViewModel(
            @NonNull ProductRepository repository, @NonNull ProductValidator validator) {
        this.repository = repository;
        this.validator = validator;

        // Update the enabled state of the update button based on the validation state.
        updateButtonState.addSource(
                validationState,
                state -> {
                    boolean hasNoError = state.hasNoError();
                    updateButtonState.setValue(hasNoError);
                    Timber.d("updateButtonState: %s", hasNoError);
                });
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product.setValue(product);
    }

    public LiveData<ProductEditValidationState> getValidationState() {
        return validationState;
    }

    public LiveData<Boolean> getUpdateButtonState() {
        return updateButtonState;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public LiveData<Boolean> getUpdateState() {
        return isUpdating;
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    public LiveData<Boolean> getDeleteState() {
        return isDeleting;
    }

    public LiveData<Boolean> getCreateState() {
        return isCreating;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResult;
    }

    /**
     * Loads a product by its ID. If the ID is empty, a new product is created. If the ID is not
     * empty, the product is fetched from the repository.
     *
     * @param id The ID of the product to load.
     */
    public void loadProductById(String id) {
        Product currentProduct = product.getValue();
        // Check if the product is already loaded.
        if (currentProduct == null) {
            if (id.isEmpty()) {
                // If the ID is empty, create a new product.
                product.setValue(new Product());
            } else {
                // If the ID is not empty, fetch the product from the repository.
                Product fetchedProduct = repository.getProductById(id);
                if (fetchedProduct != null) {
                    product.setValue(fetchedProduct);
                }
            }
        }
    }

    /**
     * Updates the validation state of the product. This method takes a Consumer that modifies the
     * current validation state.
     *
     * @param action The action to perform on the current validation state.
     */
    private void updateValidationState(Consumer<ProductEditValidationState> action) {
        ProductEditValidationState currentState = validationState.getValue();
        if (currentState != null) {
            action.accept(currentState);
            validationState.setValue(currentState);
        } else {
            Timber.e("Current state is null");
        }
    }

    /**
     * Validates the product name. This method updates the validation state with the result of the
     * validation.
     *
     * @param name The name of the product to validate.
     */
    public void validateName(String name) {
        updateValidationState(
                state -> {
                    String nameError = validator.validateName(name);
                    Timber.d("validateName: %s", nameError);
                    state.setNameError(nameError);
                });
    }

    /**
     * Validates the product code. This method updates the validation state with the result of the
     * validation.
     *
     * @param sellingPrice The selling price of the product to validate.
     */
    public void validateSellingPrice(Double sellingPrice) {
        updateValidationState(
                state -> {
                    String sellingPriceError = validator.validateSellingPrice(sellingPrice);
                    Timber.d("validateSellingPrice: %s", sellingPriceError);
                    state.setSellingPriceError(sellingPriceError);
                });
    }

    /**
     * Validates the purchase price. This method updates the validation state with the result of the
     * validation.
     *
     * @param purchasePrice The purchase price of the product to validate.
     */
    public void validatePurchasePrice(Double purchasePrice) {
        Product p = product.getValue();
        if (p != null) {
            updateValidationState(
                    state -> {
                        String purchasePriceError =
                                validator.validatePurchasePrice(purchasePrice, p.getSellingPrice());
                        Timber.d("validatePurchasePrice: %s", purchasePriceError);
                        state.setPurchasePriceError(purchasePriceError);
                    });
        } else {
            Timber.e("Product is null when validating purchase price");
        }
    }

    /**
     * Validates the discount price. This method updates the validation state with the result of the
     * validation.
     *
     * @param discountPrice The discount price of the product to validate.
     */
    public void validateDiscountPrice(Double discountPrice) {
        Product p = product.getValue();
        if (p != null) {
            updateValidationState(
                    state -> {
                        String discountPriceError =
                                validator.validateDiscountPrice(discountPrice, p.getSellingPrice());
                        Timber.d("validateDiscountPrice: %s", discountPriceError);
                        state.setDiscountPriceError(discountPriceError);
                    });
        } else {
            Timber.e("Product is null when validating discount price");
        }
    }

    /**
     * Validates the product description. This method updates the validation state with the result
     * of the validation.
     *
     * @param description The description of the product to validate.
     */
    public void validateDescription(String description) {
        updateValidationState(
                state -> {
                    String descriptionError = validator.validateDescription(description);
                    Timber.d("validateDescription: %s", descriptionError);
                    state.setDescriptionError(descriptionError);
                });
    }

    /**
     * Validates the product note. This method updates the validation state with the result of the
     * validation.
     *
     * @param note The note of the product to validate.
     */
    public void validateNote(String note) {
        updateValidationState(
                state -> {
                    String noteError = validator.validateNote(note);
                    Timber.d("validateNote: %s", noteError);
                    state.setNoteError(noteError);
                });
    }

    public void validateBrand(Brand brand) {
        updateValidationState(
                state -> {
                    String brandError = validator.validateBrand(brand);
                    Timber.d("validateBrand: %s", brandError);
                    state.setBrandError(brandError);
                });
    }

    public void validateCategory(Category category) {
        updateValidationState(
                state -> {
                    String categoryError = validator.validateCategory(category);
                    Timber.d("validateCategory: %s", categoryError);
                    state.setCategoryError(categoryError);
                });
    }

    /**
     * Called when the user clicks the "Finish" button to update the product.
     *
     * <p>This method retrieves the current product from the LiveData and updates it in the
     * repository. It also sets the loading state and update result LiveData.
     *
     * @param view The view that was clicked.
     * @noinspection unused
     */
    public void onUpdateButtonClick(View view) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isUpdating.setValue(true);
            repository.updateProduct(
                    currentProduct,
                    isSuccessful -> {
                        isUpdating.setValue(false);
                        updateResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Product is null when trying to update");
        }
    }

    /** Called when the user clicks the "Delete" button to delete the product. */
    public void deleteProduct() {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isDeleting.setValue(true);
            repository.deleteProduct(
                    currentProduct,
                    isSuccessful -> {
                        isDeleting.setValue(false);
                        deleteResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Product is null when trying to delete");
        }
    }

    /**
     * Called when the user clicks the "Create" button to create the product.
     *
     * @param view The view that was clicked.
     * @noinspection unused
     */
    public void onCreateButtonClick(@NonNull View view) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isCreating.setValue(true);
            repository.createProduct(
                    currentProduct,
                    isSuccessful -> {
                        isCreating.setValue(false);
                        createResult.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Product is null when trying to create");
        }
    }
}
