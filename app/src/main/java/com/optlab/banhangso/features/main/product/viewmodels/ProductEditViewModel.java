package com.optlab.banhangso.features.main.product.viewmodels;

import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_BRAND;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_CATEGORY;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_DESCRIPTION;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_DISCOUNT_PRICE;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_NAME;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_NOTE;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_PURCHASE_PRICE;
import static com.optlab.banhangso.internal.utilities.Constants.Product.KEY_SELLING_PRICE;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.ObservableArrayMap;
import androidx.databinding.ObservableMap;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.features.main.product.models.ProductUiModel;
import com.optlab.banhangso.features.main.product.models.mappers.ProductUiModelMapper;
import com.optlab.banhangso.internal.validators.ProductValidator;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.repositories.interfaces.ProductRepository;

import java.util.Objects;
import java.util.function.Consumer;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class ProductEditViewModel extends ViewModel {
    private final ProductValidator validator;
    private final ProductRepository productRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> isCreateMode = new MutableLiveData<>(true);
    private final MutableLiveData<ProductUiModel> productUiModel = new MutableLiveData<>();
    private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<Boolean> createResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteResult = new MutableLiveData<>();
    private final MutableLiveData<Boolean> updateResult = new MutableLiveData<>();
    private final ObservableMap<String, String> errors = new ObservableArrayMap<>();
    private Observable.OnPropertyChangedCallback productOnPropertyChangedCallback;

    @Inject
    public ProductEditViewModel(
            @NonNull ProductRepository productRepository, @NonNull ProductValidator validator) {
        this.productRepository = productRepository;
        this.validator = validator;
        initProductInputsListener();
    }

    private void initErrors() {
        canSubmit.setValue(!Boolean.TRUE.equals(isCreateMode.getValue()));

        errors.put(KEY_NAME, "");
        errors.put(KEY_SELLING_PRICE, "");
        errors.put(KEY_PURCHASE_PRICE, "");
        errors.put(KEY_DISCOUNT_PRICE, "");
        errors.put(KEY_DESCRIPTION, "");
        errors.put(KEY_NOTE, "");
        errors.put(KEY_BRAND, "");
        errors.put(KEY_CATEGORY, "");
    }

    @Override
    protected void onCleared() {
        productOnPropertyChangedCallback = null;
        super.onCleared();
    }

    private void initProductInputsListener() {
        if (productOnPropertyChangedCallback != null) {
            return;
        }

        productOnPropertyChangedCallback =
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        validateProperty(propertyId);
                    }

                    private void validateProperty(int propertyId) {
                        switch (propertyId) {
                            case BR.name -> validateName();
                            case BR.sellingPrice -> validateSellingPrice();
                            case BR.purchasePrice -> validatePurchasePrice();
                            case BR.discountPrice -> validateDiscountPrice();
                            case BR.description -> validateDescription();
                            case BR.note -> validateNote();
                            case BR.brand -> validateBrand();
                            case BR.category -> validateCategory();
                            default -> Timber.w("Unknown property changed: %s", propertyId);
                        }
                    }
                };

        // Replace observeForever with a custom setter for productUiModel
        productUiModel.observeForever(this::attachPropertyChangedCallback);
    }

    // Helper method to attach the callback to any ProductUiModel instance
    private void attachPropertyChangedCallback(ProductUiModel product) {
        if (product != null && productOnPropertyChangedCallback != null) {
            product.addOnPropertyChangedCallback(productOnPropertyChangedCallback);
        }
    }

    public void setCreateMode(boolean isCreateMode) {
        this.isCreateMode.setValue(isCreateMode);
        if (isCreateMode) {
            initErrors();
        } else {
            canSubmit.setValue(true);
        }
    }

    public boolean isCreateMode() {
        return Boolean.TRUE.equals(isCreateMode.getValue());
    }

    public LiveData<ProductUiModel> getProductUiModel() {
        return productUiModel;
    }

    public LiveData<Boolean> canSubmit() {
        return canSubmit;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getCreateResult() {
        return createResult;
    }

    public LiveData<Boolean> getUpdateResult() {
        return updateResult;
    }

    public LiveData<Boolean> getDeleteResult() {
        return deleteResult;
    }

    /**
     * @noinspection LombokGetterMayBeUsed
     */
    public ObservableMap<String, String> getErrors() {
        return errors;
    }

    /**
     * Loads a product by its ID. If the ID is empty, a new product is created. If the ID is not
     * empty, the product is fetched from the repository.
     *
     * @param id The ID of the product to load.
     */
    public void loadProductById(String id) {
        ProductUiModel currentProduct = productUiModel.getValue();
        // Check if the product is already loaded.
        if (currentProduct == null) {
            if (id.isEmpty()) {
                // If the ID is empty, create a new product.
                productUiModel.setValue(new ProductUiModel());
            } else {
                Disposable disposable =
                        productRepository
                                .getProduct(id)
                                .subscribeOn(Schedulers.io())
                                .doOnSubscribe(__ -> isLoading.postValue(true))
                                .observeOn(AndroidSchedulers.mainThread())
                                .doFinally(() -> isLoading.setValue(false))
                                .subscribe(this::onGetProductSuccess, this::onGetProductError);

                disposables.add(disposable);
            }
        }
    }

    private void onGetProductSuccess(Result<Product> result) {
        if (result instanceof Result.Success<Product> success) {
            Product product = success.getData();
            productUiModel.setValue(
                    ProductUiModelMapper.fromDomain(Objects.requireNonNull(product)));
        } else if (result instanceof Result.Failure<Product> failure) {

        }
    }

    private void onGetProductError(Throwable throwable) {
        Timber.e(throwable, "There was an error getting the product: %s", throwable.getMessage());
    }

    private void setErrors(@NonNull Consumer<ObservableMap<String, String>> mapConsumer) {
        ObservableMap<String, String> map = errors;
        mapConsumer.accept(map); // Apply the consumer to the map
        errors.putAll(map); // Update the errors map with the new values
        updateCanSubmit(); // Update the canSubmit state based on the errors
    }

    private void updateCanSubmit() {
        boolean isEmpty = errors.isEmpty();
        Timber.d("Errors is empty? %b", isEmpty);
        canSubmit.setValue(errors.isEmpty());
    }

    /**
     * Validates the product name. This method updates the validation state with the result of the
     * validation.
     */
    public void validateName() {
        setErrors(
                errorMap -> {
                    String error =
                            validator.validateName(
                                    Objects.requireNonNull(productUiModel.getValue()).getName());
                    if (error.isBlank()) {
                        errors.remove(KEY_NAME);
                    } else {
                        errorMap.put(KEY_NAME, error);
                    }
                });
    }

    /**
     * Validates the product code. This method updates the validation state with the result of the
     * validation.
     */
    public void validateSellingPrice() {
        setErrors(
                errorMap -> {
                    String error =
                            validator.validateSellingPrice(
                                    Objects.requireNonNull(productUiModel.getValue())
                                            .getSellingPrice());
                    if (error.isBlank()) {
                        errors.remove(KEY_SELLING_PRICE);
                    } else {
                        errorMap.put(KEY_SELLING_PRICE, error);
                    }
                });
    }

    /**
     * Validates the purchase price. This method updates the validation state with the result of the
     * validation.
     */
    public void validatePurchasePrice() {
        setErrors(
                errorMap -> {
                    double purchasePrice =
                            Objects.requireNonNull(productUiModel.getValue()).getPurchasePrice();
                    double sellingPrice =
                            Objects.requireNonNull(productUiModel.getValue()).getSellingPrice();

                    String error = validator.validatePurchasePrice(purchasePrice, sellingPrice);
                    if (error.isBlank()) {
                        errors.remove(KEY_PURCHASE_PRICE);
                    } else {
                        errorMap.put(KEY_PURCHASE_PRICE, error);
                    }
                });
    }

    /**
     * Validates the discount price. This method updates the validation state with the result of the
     * validation.
     */
    public void validateDiscountPrice() {
        setErrors(
                errorMap -> {
                    double discountPrice =
                            Objects.requireNonNull(productUiModel.getValue()).getSellingPrice();
                    double sellingPrice =
                            Objects.requireNonNull(productUiModel.getValue()).getSellingPrice();

                    String error = validator.validateDiscountPrice(discountPrice, sellingPrice);
                    if (error.isBlank()) {
                        errors.remove(KEY_DISCOUNT_PRICE);
                    } else {
                        errorMap.put(KEY_DISCOUNT_PRICE, error);
                    }
                });
    }

    /**
     * Validates the product description. This method updates the validation state with the result
     * of the validation.
     */
    public void validateDescription() {
        setErrors(
                errorMap -> {
                    String error =
                            validator.validateDescription(
                                    Objects.requireNonNull(productUiModel.getValue())
                                            .getDescription());
                    if (error.isBlank()) {
                        errors.remove(KEY_DESCRIPTION);
                    } else {
                        errorMap.put(KEY_DESCRIPTION, error);
                    }
                });
    }

    /**
     * Validates the product note. This method updates the validation state with the result of the
     * validation.
     */
    public void validateNote() {
        setErrors(
                errorMap -> {
                    String error =
                            validator.validateNote(
                                    Objects.requireNonNull(productUiModel.getValue()).getNote());
                    if (error.isBlank()) {
                        errors.remove(KEY_NOTE);
                    } else {
                        errorMap.put(KEY_NOTE, error);
                    }
                });
    }

    public void validateBrand() {
        setErrors(
                errorMap -> {
                    String error = validator.validateBrand(
                            Objects.requireNonNull(productUiModel.getValue()).getBrand());
                    if (error.isBlank()) {
                        errors.remove(KEY_BRAND);
                    } else {
                        errorMap.put(KEY_BRAND, error);
                    }
                });
    }

    public void validateCategory() {
        setErrors(
                errorMap -> {
                    String error = validator.validateCategory(
                            Objects.requireNonNull(productUiModel.getValue()).getCategory());
                    if (error.isBlank()) {
                        errors.remove(KEY_CATEGORY);
                    } else {
                        errorMap.put(KEY_CATEGORY, error);
                    }
                });
    }

    /**
     * @noinspection unused
     */
    public void onUpdate(View view) {
        ProductUiModel productUiModelValue = productUiModel.getValue();
        if (productUiModelValue != null) {
            Product product = ProductUiModelMapper.toDomain(productUiModelValue);
            Disposable disposable =
                    productRepository
                            .updateProduct(product)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(__ -> isLoading.postValue(true))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> isLoading.setValue(false))
                            .subscribe(
                                    result -> updateResult.setValue(result instanceof Result.Success),
                                    throwable -> {
                                        Timber.e(throwable, "Error updating product");
                                        updateResult.setValue(false);
                                    });

            disposables.add(disposable);
        } else {
            Timber.e("Product is null when trying to update");
        }
    }

    /** Called when the user clicks the "Delete" button to delete the product. */
    public void onDelete() {
        ProductUiModel productUiModelValue = productUiModel.getValue();
        if (productUiModelValue != null) {
            Disposable disposable =
                    productRepository
                            .deleteProduct(productUiModelValue.getId())
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(__ -> isLoading.postValue(true))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> isLoading.setValue(false))
                            .subscribe(
                                    result -> deleteResult.setValue(result instanceof Result.Success),
                                    throwable -> {
                                        Timber.e(throwable, "Error deleting product");
                                        deleteResult.setValue(false);
                                    });
            disposables.add(disposable);
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
    public void onCreate(@NonNull View view) {
        ProductUiModel productUiModelValue = productUiModel.getValue();
        if (productUiModelValue != null) {
            Product product = ProductUiModelMapper.toDomain(productUiModelValue);
            Disposable disposable =
                    productRepository
                            .createProduct(product)
                            .subscribeOn(Schedulers.io())
                            .doOnSubscribe(__ -> isLoading.postValue(true))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doFinally(() -> isLoading.setValue(false))
                            .subscribe(
                                    result -> createResult.setValue(result instanceof Result.Success),
                                    throwable -> {
                                        Timber.e(throwable, "Error creating product");
                                        createResult.setValue(false);
                                    });
            disposables.add(disposable);
        } else {
            Timber.e("Product is null when trying to create");
        }
    }
}
