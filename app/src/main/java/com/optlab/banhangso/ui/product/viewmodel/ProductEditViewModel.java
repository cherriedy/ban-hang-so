package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<String> nameErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> sellingPriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> purchasePriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> discountPriceErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> descriptionErrorLiveData = new MutableLiveData<>();
    private final MutableLiveData<String> noteErrorLiveData = new MutableLiveData<>();
    private final ProductValidator productValidator;

    @Inject
    public ProductEditViewModel(@NonNull ProductRepository repository,
                                @NonNull ProductValidator productValidator) {
        this.repository = repository;
        this.productValidator = productValidator;
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

    public void loadProductById(String id) {
        // Prevent reloading a new object if the current product has unsaved changes,
        // which would cause data loss.
        Product currentProduct = product.getValue();
        Product fetchedProduct = repository.getProductById(id);
        if (currentProduct == null && fetchedProduct != null) {
            product.setValue(fetchedProduct);
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
}
