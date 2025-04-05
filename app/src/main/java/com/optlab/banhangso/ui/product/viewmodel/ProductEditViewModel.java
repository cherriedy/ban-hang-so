package com.optlab.banhangso.ui.product.viewmodel;

import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;
import com.optlab.banhangso.util.validator.ProductValidator;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import timber.log.Timber;

@HiltViewModel
public class ProductEditViewModel extends ViewModel {
    private final ProductRepository repository;
    private final ProductValidator productValidator;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<String> nameErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> sellingPriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> purchasePriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> discountPriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> descriptionErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> noteErrorLiveData = new MutableLiveData<>();
    private final MediatorLiveData<Boolean> updateButtonStateLiveDate = new MediatorLiveData<>();
    private final MutableLiveData<Boolean> isUpdatingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isDeletingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCreatingLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResultLiveData = new MutableLiveData<>();

    @Inject
    public ProductEditViewModel(@NonNull ProductRepository repository,
                                @NonNull ProductValidator productValidator) {
        this.repository = repository;
        this.productValidator = productValidator;
        setUpdateButtonEnabled();
    }

    /**
     * Sets up the MediatorLiveData to observe the error states of the input fields
     */
    private void setUpdateButtonEnabled() {
        updateButtonStateLiveDate.addSource(nameErrorLiveData, unused -> updateButtonState());
        updateButtonStateLiveDate.addSource(sellingPriceErrorLiveData, unused -> updateButtonState());
        updateButtonStateLiveDate.addSource(purchasePriceErrorLiveData, unused -> updateButtonState());
        updateButtonStateLiveDate.addSource(discountPriceErrorLiveData, unused -> updateButtonState());
        updateButtonStateLiveDate.addSource(descriptionErrorLiveData, unused -> updateButtonState());
        updateButtonStateLiveDate.addSource(noteErrorLiveData, unused -> updateButtonState());
    }

    /**
     * Updates the state of the update button based on the error states of the input fields.
     */
    public void updateButtonState() {
        boolean isEnabled = TextUtils.isEmpty(nameErrorLiveData.getValue())
                && TextUtils.isEmpty(sellingPriceErrorLiveData.getValue())
                && TextUtils.isEmpty(purchasePriceErrorLiveData.getValue())
                && TextUtils.isEmpty(discountPriceErrorLiveData.getValue())
                && TextUtils.isEmpty(descriptionErrorLiveData.getValue())
                && TextUtils.isEmpty(noteErrorLiveData.getValue());
        updateButtonStateLiveDate.setValue(isEnabled);
    }

    public void validateName(String name) {
        nameErrorLiveData.setValue(productValidator.validateName(name));
        Timber.d("validateName: %s", nameErrorLiveData.getValue());
    }

    public void validateSellingPrice(Double sellingPrice) {
        sellingPriceErrorLiveData.setValue(productValidator.validateSellingPrice(sellingPrice));
        Timber.d("validateSellingPrice: %s", sellingPriceErrorLiveData.getValue());
    }

    public void validatePurchasePrice(Double purchasePrice) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            purchasePriceErrorLiveData.setValue(
                    productValidator.validatePurchasePrice(purchasePrice, currentProduct.getSellingPrice()));
            Timber.d("validatePurchasePrice: %s", purchasePriceErrorLiveData.getValue());
        }
    }

    public void validateDiscountPrice(Double discountPrice) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            discountPriceErrorLiveData.setValue(
                    productValidator.validateDiscountPrice(discountPrice, currentProduct.getSellingPrice()));
            Timber.d("validateDiscountPrice: %s", discountPriceErrorLiveData.getValue());
        }
    }

    public void validateDescription(String description) {
        descriptionErrorLiveData.setValue(productValidator.validateDescription(description));
        Timber.d("validateDescription: %s", descriptionErrorLiveData.getValue());
    }

    public void validateNote(String note) {
        noteErrorLiveData.setValue(productValidator.validateNote(note));
        Timber.d("validateNote: %s", noteErrorLiveData.getValue());
    }

    /**
     * Loads a product by its ID. If the ID is empty, a new product is created. If the ID is not
     * empty, the product is fetched from the repository.
     *
     * @param id The ID of the product to load
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

    public LiveData<Product> getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product.setValue(product);
    }

    public LiveData<String> getNameError() {
        return nameErrorLiveData;
    }

    public LiveData<String> getSellingPriceError() {
        return sellingPriceErrorLiveData;
    }

    public LiveData<String> getPurchasePriceError() {
        return purchasePriceErrorLiveData;
    }

    public LiveData<String> getDiscountPriceError() {
        return discountPriceErrorLiveData;
    }

    public LiveData<String> getDescriptionError() {
        return descriptionErrorLiveData;
    }

    public LiveData<String> getNoteError() {
        return noteErrorLiveData;
    }

    public LiveData<Boolean> getUpdateButtonState() {
        return updateButtonStateLiveDate;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResultLiveData;
    }

    public LiveData<Boolean> getUpdateState() {
        return isUpdatingLiveData;
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResultLiveData;
    }

    public LiveData<Boolean> getDeleteState() {
        return isDeletingLiveData;
    }

    /**
     * Called when the user clicks the "Finish" button to update the product.
     *
     * <p> This method retrieves the current product from the LiveData and updates it in the
     * repository. It also sets the loading state and update result LiveData.
     *
     * @param view The view that was clicked
     * @noinspection unused
     */
    public void onUpdateButtonClick(View view) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isUpdatingLiveData.setValue(true);
            repository.updateProduct(
                    currentProduct, isSuccessful -> {
                        isUpdatingLiveData.setValue(false);
                        updateResultLiveData.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Product is null when trying to update");
        }
    }

    public void delete() {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isDeletingLiveData.setValue(true);
            repository.deleteProduct(
                    currentProduct, isSuccessful -> {
                        isDeletingLiveData.setValue(false);
                        deleteResultLiveData.setValue(isSuccessful);
                    });
        } else {
            Timber.e("Product is null when trying to delete");
        }
    }

    public LiveData<Boolean> getCreateState() {
        return isCreatingLiveData;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResultLiveData;
    }

    /**
     * @noinspection unused
     */
    public void onCreateButtonClick(@NonNull View view) {
        Product currentProduct = product.getValue();
        if (currentProduct != null) {
            isCreatingLiveData.setValue(true);
            repository.createProduct(currentProduct, isSuccessful -> {
                isCreatingLiveData.setValue(false);
                createResultLiveData.setValue(isSuccessful);
            });
        } else {
            Timber.e("Product is null when trying to create");
        }
    }
}
