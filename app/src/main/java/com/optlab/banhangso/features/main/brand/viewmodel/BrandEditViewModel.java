package com.optlab.banhangso.features.main.brand.viewmodel;

import static com.optlab.banhangso.features.main.brand.Constants.ERROR_NAME;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.brand.BrandValidator;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.main.brand.models.mappers.BrandUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Brand;
import com.optlab.banhangso.repositories.interfaces.BrandRepository;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class BrandEditViewModel extends UiViewModel<BrandUiModel> {

  private final BrandRepository brandRepository;
  private final BrandValidator brandValidator;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();
  private final MutableLiveData<Boolean> operationCompleted = new MutableLiveData<>();

  private Observable.OnPropertyChangedCallback brandOnPropertyChangedCallback;

  @Inject
  public BrandEditViewModel(
      @NonNull BrandRepository brandRepository, @NonNull BrandValidator brandValidator) {
    this.brandRepository = brandRepository;
    this.brandValidator = brandValidator;

    initBrandChangedListener();
  }

  private void initBrandChangedListener() {
    uiModel.observeForever(
        brand -> {
          if (brandOnPropertyChangedCallback == null) {
            brandOnPropertyChangedCallback =
                new Observable.OnPropertyChangedCallback() {
                  @Override
                  public void onPropertyChanged(Observable sender, int propertyId) {
                    if (propertyId == BR.name) {
                      validateName();
                    }
                  }

                  private void validateName() {
                    validateField(ERROR_NAME, BrandUiModel::getName, brandValidator::validateName);
                  }
                };
          }

          brand.addOnPropertyChangedCallback(brandOnPropertyChangedCallback);
        });
  }

  @Override
  protected void onValidationComplete() {
    // This method is called when the validation process is complete.
    canSubmit.setValue(errors.isEmpty());
  }

  public void loadBrandById(@NonNull String brandId) {
    BrandUiModel brand = uiModel.getValue();
    if (brand == null && brandId.isBlank()) {
      uiModel.setValue(new BrandUiModel());
    } else {
      Disposable disposable =
          brandRepository
              .getBrand(brandId)
              .subscribeOn(Schedulers.io())
              .doOnSubscribe(__ -> isLoading.postValue(true))
              .observeOn(AndroidSchedulers.mainThread())
              .doFinally(() -> isLoading.setValue(false))
              .subscribe(this::onGetBrandSuccess, this::onGetBrandError);

      disposables.add(disposable);
    }
  }

  public void setIsEditing(boolean value) {
    isEditing.setValue(value);
    canSubmit.setValue(value);
  }

  @NonNull public LiveData<Boolean> isEditing() {
    return isEditing;
  }

  @NonNull public LiveData<Boolean> canSubmit() {
    return canSubmit;
  }

  @NonNull public LiveData<Boolean> getOperationCompleted() {
    return operationCompleted;
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(@NonNull View view) {
    Brand brand = BrandUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        brandRepository
            .updateBrand(brand)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onUpdateBrandSuccess, this::onUpdateBrandError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onCreate(@NonNull View view) {
    Brand brand = BrandUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        brandRepository
            .createBrand(brand)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onCreateBrandSuccess, this::onCreateBrandError);

    disposables.add(disposable);
  }

  private void onCreateBrandSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_brand_create_success);
      operationCompleted.setValue(true);
      isEditing.setValue(false);
    } else if (result instanceof Result.Failure<Void> failure) {
      operationCompleted.setValue(false);
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onCreateBrandError(Throwable throwable) {
    operationCompleted.setValue(false);
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error creating brand data: %s", throwable.getMessage());
  }

  private void onUpdateBrandSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      operationCompleted.setValue(true);
      messageResId.setValue(R.string.notify_brand_update_success);
      isEditing.setValue(false);
    } else if (result instanceof Result.Failure<Void> failure) {
      operationCompleted.setValue(false);
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onUpdateBrandError(Throwable throwable) {
    operationCompleted.setValue(false);
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error updating brand data: %s", throwable.getMessage());
  }

  private void onGetBrandSuccess(Result<Brand> result) {
    if (result instanceof Result.Success<Brand> success) {
      BrandUiModel brandUiModel =
          BrandUiModelMapper.fromDomain(Objects.requireNonNull(success.getData()));
      uiModel.setValue(brandUiModel);
    } else if (result instanceof Result.Failure<Brand> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_brand_not_found);
      } else if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetBrandError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error getting brand data: %s", throwable.getMessage());
  }
}
