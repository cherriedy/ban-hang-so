package com.optlab.banhangso.ui.product.viewmodel;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.repository.ProductRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

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

  @Inject
  public ProductEditViewModel(@NonNull ProductRepository repository) {
    this.repository = repository;
  }

  public void validateName(String name) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "name", name);
    // if (!violations.isEmpty()) {
    //   nameErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   nameErrorLiveData.setValue(null);
    // }
  }

  public void validateSellingPrice(Double sellingPrice) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "sellingPrice", sellingPrice);
    // if (!violations.isEmpty()) {
    //   sellingPriceErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   sellingPriceErrorLiveData.setValue(null);
    // }
  }

  public void validatePurchasePrice(Double purchasePrice) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "purchasePrice", purchasePrice);
    // if (!violations.isEmpty()) {
    //   purchasePriceErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   purchasePriceErrorLiveData.setValue(null);
    // }
  }

  public void validateDiscountPrice(Double discountPrice) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "discountPrice", discountPrice);
    // if (!violations.isEmpty()) {
    //   discountPriceErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   discountPriceErrorLiveData.setValue(null);
    // }
  }

  public void validateDescription(String description) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "description", description);
    // if (!violations.isEmpty()) {
    //   descriptionErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   descriptionErrorLiveData.setValue(null);
    // }
  }

  public void validateNote(String note) {
    // Set<ConstraintViolation<Product>> violations =
    //     validator.validateValue(Product.class, "note", note);
    // if (!violations.isEmpty()) {
    //   noteErrorLiveData.setValue(violations.iterator().next().getMessage());
    // } else {
    //   noteErrorLiveData.setValue(null);
    // }
  }

  public void loadProductById(String id) {
    // Prevent reloading a new object if the current product has unsaved changes
    Product currentProduct = product.getValue();
    Product fetchedProduct = repository.getProductById(id);
    if (currentProduct == null) {
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
