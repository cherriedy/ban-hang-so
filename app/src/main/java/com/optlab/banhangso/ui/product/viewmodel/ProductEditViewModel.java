package com.optlab.banhangso.ui.product.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;

public class ProductEditViewModel extends ViewModel {

    public static final String KEY_SELECTED_PRODUCT = "selected_product_id";
    private static final String KEY_FOCUSED_VIEW = "focused_view_id";
    private final SavedStateHandle savedStateHandle;
    private final ProductRepository repository;
    private final MutableLiveData<Product> product = new MutableLiveData<>();
    private final MutableLiveData<Boolean> nameError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> sellingPriceError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> purchasePriceError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> discountPercentageError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> productDescriptionError = new MutableLiveData<>();
    private final MutableLiveData<Boolean> productNoteError = new MutableLiveData<>();

    public ProductEditViewModel(SavedStateHandle savedStateHandle, ProductRepository repository) {
        this.savedStateHandle = savedStateHandle;
        this.repository = repository;
    }

    public Long getKeySelectedProduct() {
        return savedStateHandle.get(KEY_SELECTED_PRODUCT);
    }

//    public void setKeySelectedProduct(long productId) {
//        savedStateHandle.set(KEY_SELECTED_PRODUCT, productId);
//    }

    public Integer getKeyFocusedView() {
        return savedStateHandle.get(KEY_FOCUSED_VIEW);
    }

    public void setKeyFocusedView(int viewId) {
        savedStateHandle.set(KEY_FOCUSED_VIEW, viewId);
    }

    public void loadProductById(long id) {
        // Check whether `product` is null, preventing reloading new object
        // if there's a configuration change which leads to lose the current
        // changes in fields.
        Product currentProduct = product.getValue();
        Product fetchedProduct = repository.getProductById(id);
        if (currentProduct == null) {
            product.setValue(fetchedProduct);
            savedStateHandle.set(KEY_SELECTED_PRODUCT, id);
        }
    }

    public void saveProduct(Product updatedProduct) {
        if (updatedProduct.getId() == 0L) {
            repository.addProduct(updatedProduct);
        } else {
            repository.updateProduct(updatedProduct);
        }
    }

    public LiveData<Product> getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product.setValue(product);
    }

    public LiveData<Boolean> getNameError() {
        return nameError;
    }

    public LiveData<Boolean> getSellingPriceError() {
        return sellingPriceError;
    }

    public LiveData<Boolean> getPurchasePriceError() {
        return purchasePriceError;
    }

    public LiveData<Boolean> getDiscountPercentageError() {
        return discountPercentageError;
    }

    public LiveData<Boolean> getProductDescriptionError() {
        return productDescriptionError;
    }

    public LiveData<Boolean> getProductNoteError() {
        return productNoteError;
    }

    public void validateName(String name) {
        if (!TextUtils.isEmpty(name)) {
            nameError.setValue(false);
        } else {
            nameError.setValue(true);
        }
    }

    public void validateSellingPrice(double price) {
        if (price == 0.0) {
            sellingPriceError.setValue(true);
        } else {
            sellingPriceError.setValue(false);
        }
    }

    public void validatePurchasePrice(double price) {
        if (price == 0.0) {
            purchasePriceError.setValue(true);
        } else {
            purchasePriceError.setValue(false);
        }
    }

    public void validateDiscountPrice(double price) {
        // Get the current instance of product.
        Product currentProduct = product.getValue();

        // Calculate the discount price to see if it is larger than the selling price.
        if (price >= currentProduct.getSellingPrice()) {
            final int discountPrice = ((int) price) / 10;

            // Create a handler to create animation when deleting the last digit.
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (currentProduct != null) {
                    currentProduct.setDiscountPrice(discountPrice);
                    product.setValue(currentProduct); // Triggers UI update
                }
            }, 50);
        }
    }

    public void validateNote(String note) {
        if (TextUtils.isEmpty(note) && note.length() > 100) {
            productNoteError.setValue(true);
        } else {
            productNoteError.setValue(false);
        }
    }

    public void validateDescription(String description) {
        if (TextUtils.isEmpty(description) && description.length() > 1000) {
            productDescriptionError.setValue(true);
        } else {
            productDescriptionError.setValue(false);
        }
    }
}
