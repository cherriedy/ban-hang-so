package com.optlab.banhangso.features.main.product.viewmodels;

import static com.optlab.banhangso.features.main.product.Constants.KEY_BRAND;
import static com.optlab.banhangso.features.main.product.Constants.KEY_CATEGORY;
import static com.optlab.banhangso.features.main.product.Constants.KEY_DESCRIPTION;
import static com.optlab.banhangso.features.main.product.Constants.KEY_DISCOUNT_PRICE;
import static com.optlab.banhangso.features.main.product.Constants.KEY_NAME;
import static com.optlab.banhangso.features.main.product.Constants.KEY_NOTE;
import static com.optlab.banhangso.features.main.product.Constants.KEY_PURCHASE_PRICE;
import static com.optlab.banhangso.features.main.product.Constants.KEY_SELLING_PRICE;
import static dagger.hilt.android.internal.ThreadUtil.isMainThread;

import android.net.Uri;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import com.optlab.banhangso.features.main.product.ProductValidator;
import com.optlab.banhangso.features.main.product.models.ProductUiModel;
import com.optlab.banhangso.features.main.product.models.mappers.ProductUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.internal.utilities.uploaders.qualifiers.ProductImageUploader;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.application.UploadableImage;
import com.optlab.banhangso.models.domain.Product;
import com.optlab.banhangso.repositories.interfaces.ProductRepository;
import com.optlab.banhangso.services.ImageUploader;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class ProductEditViewModel extends UiViewModel<ProductUiModel> {

  private final ProductValidator productValidator;
  private final ProductRepository productRepository;
  private final ImageUploader imageUploader;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();
  private final MutableLiveData<Boolean> operationCompleted = new MutableLiveData<>();

  private final List<UploadableImage> internalUploadableImages = new ArrayList<>();
  private final MutableLiveData<List<UploadableImage>> uploadableImage = new MutableLiveData<>();
  private final MutableLiveData<Pair<Integer, UploadableImage>> imagePair = new MutableLiveData<>();

  private Observable.OnPropertyChangedCallback productOnPropertyChangedCallback;

  @Inject
  public ProductEditViewModel(
      @NonNull ProductRepository productRepository,
      @NonNull ProductValidator productValidator,
      @NonNull @ProductImageUploader ImageUploader imageUploader) {
    this.productRepository = productRepository;
    this.productValidator = productValidator;
    this.imageUploader = imageUploader;
    initProductInputsListener();

  }

  @Override
  protected void onCleared() {
    productOnPropertyChangedCallback = null;
    super.onCleared();
  }

  @Override
  protected void onValidationComplete() {
    canSubmit.setValue(errors.isEmpty());
  }

  public void setIsEditing(boolean value) {
    isEditing.setValue(value);
    canSubmit.setValue(value);
  }

  public boolean isEditing() {
    return Boolean.TRUE.equals(isEditing.getValue());
  }

  @NonNull public LiveData<ProductUiModel> getProductUiModel() {
    return uiModel;
  }

  @NonNull public LiveData<Boolean> canSubmit() {
    return canSubmit;
  }

  @NonNull public LiveData<Boolean> getOperationCompleted() {
    return operationCompleted;
  }

  @NonNull public LiveData<Pair<Integer, UploadableImage>> getImagePair() {
    return imagePair;
  }

  @NonNull public LiveData<List<UploadableImage>> getUploadableImage() {
    return uploadableImage;
  }

  /**
   * Loads a product by its ID. If the ID is empty, a new product is created. If the ID is not
   * empty, the product is fetched from the repository.
   *
   * @param productId The ID of the product to load.
   */
  public void loadProductById(String productId) {
    ProductUiModel currentProduct = uiModel.getValue();
    // Check if the product is already loaded.
    if (currentProduct == null && productId.isEmpty()) {
      // If the ID is empty, create a new product.
      uiModel.setValue(new ProductUiModel());
    } else {
      Disposable disposable =
          productRepository
              .getProduct(productId)
              .subscribeOn(Schedulers.io())
              .doOnSubscribe(__ -> isLoading.postValue(true))
              .observeOn(AndroidSchedulers.mainThread())
              .doFinally(() -> isLoading.setValue(false))
              .subscribe(this::onGetProductSuccess, this::onGetProductError);

      disposables.add(disposable);
    }
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(View view) {
    Product product = ProductUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));
    product.setImageUrls(getImageUrls()); // Set image URLs before updating

    Disposable disposable =
        productRepository
            .updateProduct(product)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(
                () -> {
                  isLoading.setValue(false);
                  operationCompleted.setValue(true);
                })
            .subscribe(this::onUpdateProductSuccess, this::onUpdateProductError);

    disposables.add(disposable);
  }

  private void onUpdateProductSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_update_product_successfully);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_product_not_found);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onUpdateProductError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error updating the product: %s", throwable.getMessage());
  }

  public void onDelete() {
    String productId = Objects.requireNonNull(uiModel.getValue()).getId();

    Disposable disposable =
        productRepository
            .deleteProduct(productId)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(
                () -> {
                  isLoading.setValue(false);
                  operationCompleted.setValue(true);
                })
            .subscribe(this::onDeleteProductSuccess, this::onDeleteProductError);

    disposables.add(disposable);
  }

  private void onDeleteProductSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_delete_product_successfully);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_product_not_found);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onDeleteProductError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error deleting the product: %s", throwable.getMessage());
  }

  /**
   * @noinspection unused
   */
  public void onCreate(@NonNull View view) {
    Product product = ProductUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));
    product.setImageUrls(getImageUrls()); // Set image URLs before creating

    Disposable disposable =
        productRepository
            .createProduct(product)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(
                () -> {
                  isLoading.setValue(false);
                  operationCompleted.setValue(true);
                })
            .subscribe(this::onCreateProductSuccess, this::onCreateProductError);

    disposables.add(disposable);
  }

  private void onCreateProductSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_create_product_successfully);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onCreateProductError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error creating the product: %s", throwable.getMessage());
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
            }
          }

          public void validateName() {
            validateField(KEY_NAME, ProductUiModel::getName, productValidator::validateName);
          }

          public void validateSellingPrice() {
            validateField(
                KEY_SELLING_PRICE,
                ProductUiModel::getSellingPrice,
                productValidator::validateSellingPrice);
          }

          public void validatePurchasePrice() {
            validateField(
                KEY_PURCHASE_PRICE,
                ProductUiModel::getPurchasePrice,
                ProductUiModel::getSellingPrice,
                productValidator::validatePurchasePrice);
          }

          public void validateDiscountPrice() {
            validateField(
                KEY_DISCOUNT_PRICE,
                ProductUiModel::getDiscountPrice,
                ProductUiModel::getSellingPrice,
                productValidator::validateDiscountPrice);
          }

          public void validateDescription() {
            validateField(
                KEY_DESCRIPTION,
                ProductUiModel::getDescription,
                productValidator::validateDescription);
          }

          public void validateNote() {
            validateField(KEY_NOTE, ProductUiModel::getNote, productValidator::validateNote);
          }

          public void validateBrand() {
            validateField(KEY_BRAND, ProductUiModel::getBrand, productValidator::validateBrand);
          }

          public void validateCategory() {
            validateField(
                KEY_CATEGORY, ProductUiModel::getCategory, productValidator::validateCategory);
          }
        };

    // Replace observeForever with a custom setter for uiModel
    uiModel.observeForever(this::attachPropertyChangedCallback);
  }

  private void attachPropertyChangedCallback(ProductUiModel product) {
    if (product != null && productOnPropertyChangedCallback != null) {
      product.addOnPropertyChangedCallback(productOnPropertyChangedCallback);
    }
  }

  private void onGetProductSuccess(Result<Product> result) {
    if (result instanceof Result.Success<Product> success) {
      Product product = success.getData();
      uiModel.setValue(ProductUiModelMapper.fromDomain(Objects.requireNonNull(product)));

      List<String> imageUrls = product.getImageUrls();
      if (imageUrls != null && !imageUrls.isEmpty()) {
        internalUploadableImages.clear();
        internalUploadableImages.addAll(
            imageUrls.stream()
                .map(UploadableImage::new)
                .collect(Collectors.toList()));

        uploadableImage.setValue(internalUploadableImages);
      }
    } else if (result instanceof Result.Failure<Product> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_product_not_found);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetProductError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error getting the product: %s", throwable.getMessage());
  }

  // ===============================================================================================

  /**
   * @noinspection SimplifyStreamApiCallChains
   */
  public void onImagesProvided(@NonNull List<Uri> uris) {
    List<UploadableImage> pendingUploadImages =
        uris.stream().map(UploadableImage::new).collect(Collectors.toList());

    internalUploadableImages.addAll(pendingUploadImages);

    uploadableImage.setValue(internalUploadableImages);

    uploadPendingImages(); // Start uploading selected images
  }

  public void uploadPendingImages() {
    List<Uri> pendingUris = new ArrayList<>();
    List<Integer> pendingIndices = new ArrayList<>();

    for (int i = 0; i < internalUploadableImages.size(); i++) {
      UploadableImage image = internalUploadableImages.get(i);
      if (image.getStatus() == UploadableImage.Status.PENDING) {
        pendingIndices.add(i);
        pendingUris.add(image.getContentUri());
      }
    }

    if (pendingIndices.isEmpty()) {
      return;
    }

    imageUploader.uploadImages(
        pendingUris,
        new ImageUploader.ImageUploadProgressListener() {
          @Override
          public void onProgress(int imageIndex, int percentage) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              UploadableImage image = internalUploadableImages.get(actualIndex);
              image.setStatus(UploadableImage.Status.UPLOADING);
              image.setProgress(percentage);
              imagePair.postValue(new Pair<>(actualIndex, image));
            }
          }

          @Override
          public void onSuccess(int imageIndex, String imageUrl) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              UploadableImage image = internalUploadableImages.get(actualIndex);
              image.setStatus(UploadableImage.Status.COMPLETED);
              image.setRemoteUrl(imageUrl);
              imagePair.postValue(new Pair<>(actualIndex, image));
            }
          }

          @Override
          public void onFailed(int imageIndex, String errorMessage) {
            if (imageIndex >= 0 && imageIndex < pendingIndices.size()) {
              int actualIndex = pendingIndices.get(imageIndex);
              UploadableImage image = internalUploadableImages.get(actualIndex);
              image.setStatus(UploadableImage.Status.FAILED);
              image.setErrorMessage(errorMessage);
              imagePair.postValue(new Pair<>(actualIndex, image));
            }
          }

          @Override
          public void onCompleted(List<String> imageUrls) {
            imageUrls.forEach(Timber::i); // Log the URLs of successfully uploaded images
          }
        });
  }

  private void updateProduct(@NonNull Consumer<ProductUiModel> consumer) {
    ProductUiModel productUiModel = uiModel.getValue();
    if (productUiModel != null) {
      consumer.accept(productUiModel);
      if (isMainThread()) {
        uiModel.setValue(productUiModel);
      } else {
        uiModel.postValue(productUiModel);
      }
    }
  }

  public void updateCategory(@NonNull CategoryUiModel categoryUiModel) {
    updateProduct(product -> product.setCategory(categoryUiModel));
  }

  public void updateBrand(@NonNull BrandUiModel brandUiModel) {
    updateProduct(product -> product.setBrand(brandUiModel));
  }

  public void updateBarcode(@NonNull String barcode) {
    updateProduct(product -> product.setBarcode(barcode));
  }

  public void removeImage(int position) {
    if (position < 0 || position >= internalUploadableImages.size()) {
      Timber.e("Unable to remove image at position %d: Invalid position", position);
    } else {
      internalUploadableImages.remove(position);
      uploadableImage.setValue(internalUploadableImages);
    }
  }

  public void retryUpload(int position) {
    if (position < 0 || position >= internalUploadableImages.size()) {
      Timber.e("Unable to retry upload for image at position %d: Invalid position", position);
    } else {
      UploadableImage image = internalUploadableImages.get(position);

      image.setProgress(0);
      image.setErrorMessage(null);
      image.setStatus(UploadableImage.Status.PENDING);

      // Update the ui state for the image.
      imagePair.setValue(new Pair<>(position, image));

      imageUploader.uploadImage(
          image.getContentUri(),
          new ImageUploader.ImageUploadProgressListener() {
            @Override
            public void onProgress(int imageIndex, int percentage) {
              image.setStatus(UploadableImage.Status.UPLOADING);
              image.setProgress(percentage);
              imagePair.postValue(new Pair<>(position, image));
            }

            @Override
            public void onSuccess(int imageIndex, String imageUrl) {
              image.setStatus(UploadableImage.Status.COMPLETED);
              image.setProgress(0);
              image.setRemoteUrl(imageUrl);
              imagePair.postValue(new Pair<>(position, image));
            }

            @Override
            public void onFailed(int imageIndex, String errorMessage) {
              image.setStatus(UploadableImage.Status.FAILED);
              image.setErrorMessage(errorMessage);
              imagePair.postValue(new Pair<>(position, image));
            }

            @Override
            public void onCompleted(List<String> imageUrls) {}
          });
    }
  }

  private List<String> getImageUrls() {
    return internalUploadableImages.stream()
        .map(UploadableImage::getRemoteUrl)
        .filter(url -> url != null && !url.isBlank())
        .collect(Collectors.toList());
  }
}
