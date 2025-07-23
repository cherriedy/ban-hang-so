package com.optlab.banhangso.features.main.store.viewmodel;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.store.models.StoreUiModel;
import com.optlab.banhangso.features.main.store.models.mappers.StoreUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.store.Store;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.repositories.interfaces.StoreRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Objects;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class StoreEditViewModel extends UiViewModel<StoreUiModel> {

  private final StoreRepository storeRepository;
  private final PreferencesRepositoryKt preferencesRepositoryKt;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> operationCompleted = new MutableLiveData<>();

  @Inject
  public StoreEditViewModel(
      StoreRepository storeRepository, PreferencesRepositoryKt preferencesRepositoryKt) {
    this.storeRepository = storeRepository;
    this.preferencesRepositoryKt = preferencesRepositoryKt;

    // Initialize the UI model with an empty StoreUiModel.
    uiModel.setValue(new StoreUiModel());
  }

  public void setIsEditing(boolean value) {
    isEditing.setValue(value);
  }

  @NonNull public LiveData<Boolean> isEditing() {
    return isEditing;
  }

  @NonNull public LiveData<Boolean> getOperationCompleted() {
    return operationCompleted;
  }

  public void loadStore() {
    Disposable disposable =
        storeRepository
            .getStore()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onGetStoreSuccess, this::onGetStoreFailure);

    disposables.add(disposable);
  }

  private void onGetStoreFailure(Throwable throwable) {
    Timber.e(throwable, "There was an error retrieving the store: %s", throwable.getMessage());
    messageResId.setValue(R.string.error_unknown);
  }

  private void onGetStoreSuccess(@NonNull Result<Store> result) {
    if (result instanceof Result.Success<Store> success) {
      StoreUiModel storeUiModel =
          StoreUiModelMapper.fromDomain(Objects.requireNonNull(success.getData()));
      uiModel.setValue(storeUiModel); // Set the UI model with the retrieved store.
    } else if (result instanceof Result.Failure<Store> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_store_not_found);
      } else if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  /**
   * @noinspection unused
   */
  public void onSave(@NonNull View view) {
    Store storeUiModel = StoreUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        storeRepository
            .setStore(storeUiModel)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onSaveStoreSuccess, this::onSaveStoreError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(@NonNull View view) {
    Store storeUiModel = StoreUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        storeRepository
            .setStore(storeUiModel)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onUpdateStoreSuccess, this::onUpdateStoreError);

    disposables.add(disposable);
  }

  private void onUpdateStoreSuccess(@NonNull Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_update_store_successful);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onUpdateStoreError(Throwable throwable) {
    Timber.e(throwable, "There was an error updating the store: %s", throwable.getMessage());
    messageResId.setValue(R.string.error_unknown);
  }

  private void onSaveStoreError(Throwable throwable) {
    Timber.e(throwable, "There was an error saving the store: %s", throwable.getMessage());
    messageResId.setValue(R.string.error_unknown);
  }

  private void onSaveStoreSuccess(@NonNull Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.notify_create_store_successful);
      operationCompleted.setValue(true);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }
}
