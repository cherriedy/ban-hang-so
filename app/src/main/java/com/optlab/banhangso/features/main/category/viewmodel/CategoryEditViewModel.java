package com.optlab.banhangso.features.main.category.viewmodel;

import static com.optlab.banhangso.features.main.category.Constants.ERROR_NAME;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.category.CategoryValidator;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import com.optlab.banhangso.features.main.category.models.mappers.CategoryUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Category;
import com.optlab.banhangso.repositories.interfaces.CategoryRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Objects;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class CategoryEditViewModel extends UiViewModel<CategoryUiModel> {

  private final CategoryRepository categoryRepository;
  private final CategoryValidator categoryValidator;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();

  private Observable.OnPropertyChangedCallback categoryOnPropertyChangedCallback;

  @Inject
  public CategoryEditViewModel(
      @NonNull CategoryRepository categoryRepository, CategoryValidator categoryValidator) {
    this.categoryRepository = categoryRepository;
    this.categoryValidator = categoryValidator;

    initCategoryChangeListener();
  }

  private void initCategoryChangeListener() {
    uiModel.observeForever(
        category -> {
          if (categoryOnPropertyChangedCallback == null) {
            categoryOnPropertyChangedCallback =
                new Observable.OnPropertyChangedCallback() {
                  @Override
                  public void onPropertyChanged(Observable sender, int propertyId) {
                    if (propertyId == BR.name) {
                      validateName();
                    }
                  }

                  private void validateName() {
                    validateField(
                        ERROR_NAME, CategoryUiModel::getName, categoryValidator::validateName);
                  }
                };
          }

          category.addOnPropertyChangedCallback(categoryOnPropertyChangedCallback);
        });
  }

  @Override
  protected void onValidationComplete() {
    // This method is called when validation is complete to check if there are any errors and update
    // the canSubmit LiveData accordingly, indicating whether the form can be submitted.
    Timber.d("onValidationComplete called, checking for errors: %b", errors.isEmpty());
    canSubmit.setValue(errors.isEmpty());
  }

  public void setIsEditing(boolean value) {
    isEditing.setValue(value);
    canSubmit.setValue(value);
  }

  @NonNull public LiveData<Boolean> canSubmit() {
    return canSubmit;
  }

  @NonNull public LiveData<Boolean> isEditing() {
    return isEditing;
  }

  public void getCategoryById(@NonNull String categoryId) {
    CategoryUiModel model = uiModel.getValue();
    if (model == null) {
      if (categoryId.isBlank()) {
        uiModel.setValue(new CategoryUiModel());
      } else {
        Disposable disposable =
            categoryRepository
                .getCategory(categoryId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(__ -> isLoading.postValue(true))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading.setValue(false))
                .subscribe(this::onGetCategorySuccess, this::onGetCategoryError);

        disposables.add(disposable);
      }
    }
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(@NonNull View view) {
    Category category = CategoryUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        categoryRepository
            .updateCategory(category)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onUpdateCategorySuccess, this::onUpdateCategoryError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onCreate(@NonNull View view) {
    Category category = CategoryUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        categoryRepository
            .createCategory(category)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onCreateCategorySuccess, this::onCreateCategoryError);

    disposables.add(disposable);
  }

  private void onCreateCategorySuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_category_create_success);
    } else if (result instanceof Result.Failure<Void> failure) {
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

  private void onCreateCategoryError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(
        throwable, "There was an error while creating the category :%s", throwable.getMessage());
  }

  private void onUpdateCategorySuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_category_update_success);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_category_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onUpdateCategoryError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(
        throwable, "There was an error while updating the category :%s", throwable.getMessage());
  }

  private void onGetCategorySuccess(Result<Category> result) {
    if (result instanceof Result.Success<Category> success) {
      CategoryUiModel categoryUiModel =
          CategoryUiModelMapper.fromDomain(Objects.requireNonNull(success.getData()));
      uiModel.setValue(categoryUiModel);
    } else if (result instanceof Result.Failure<Category> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_category_not_found);
      } else if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetCategoryError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(
        throwable, "There was an error while getting the category :%s", throwable.getMessage());
  }
}
