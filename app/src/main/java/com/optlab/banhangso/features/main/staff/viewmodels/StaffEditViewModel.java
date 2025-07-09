package com.optlab.banhangso.features.main.staff.viewmodels;

import static com.optlab.banhangso.features.main.staff.Constants.ERROR_EMAIL;
import static com.optlab.banhangso.features.main.staff.Constants.ERROR_NAME;
import static com.optlab.banhangso.features.main.staff.Constants.ERROR_PHONE;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.staff.models.StaffUiModel;
import com.optlab.banhangso.features.main.staff.models.mappers.StaffUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.features.main.staff.StaffValidator;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Staff;
import com.optlab.banhangso.repositories.interfaces.StaffRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Objects;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class StaffEditViewModel extends UiViewModel<StaffUiModel> {

  private final StaffRepository staffRepository;
  private final StaffValidator staffValidator;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();

  private Observable.OnPropertyChangedCallback staffOnPropertyChangedCallback;

  @Inject
  public StaffEditViewModel(StaffRepository staffRepository, StaffValidator staffValidator) {
    this.staffRepository = staffRepository;
    this.staffValidator = staffValidator;

    if (Objects.equals(isEditing.getValue(), Boolean.TRUE)) {
      canSubmit.setValue(true);
    }

    initStaffInputsListener();
  }

  @Override
  protected void onValidationComplete() {
    canSubmit.setValue(errors.isEmpty());
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    staffOnPropertyChangedCallback = null;
    super.onCleared();
  }

  @NonNull public LiveData<StaffUiModel> getStaff() {
    return uiModel;
  }

  public void setIsEditing(boolean value) {
    isEditing.setValue(value);
  }

  @NonNull public LiveData<Boolean> isEditing() {
    return isEditing;
  }

  @NonNull public LiveData<Boolean> canSubmit() {
    return canSubmit;
  }

  /**
   * Load staff by ID.
   *
   * <p>This method retrieves the staff information based on the provided ID. If the ID is blank, it
   * initializes an empty StaffUiModel. If the staff is successfully retrieved, it updates the
   * {@link StaffUiModel} LiveData. If there is an error during the retrieval, it sets an
   * appropriate error message in {@link #messageResId}.
   *
   * @param id The ID of the staff to load. If the ID is blank, it initializes an empty
   *     StaffUiModel.
   */
  public void loadStaffById(@NonNull String id) {
    StaffUiModel currentStaff = uiModel.getValue();
    if (currentStaff == null) {
      if (id.isBlank()) {
        uiModel.setValue(new StaffUiModel());
      } else {
        Disposable disposable =
            staffRepository
                .getStaff(id)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(__ -> isLoading.postValue(true))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading.setValue(false))
                .subscribe(this::onGetStaffSuccess, this::onGetStaffError);

        disposables.add(disposable);
      }
    }
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(@NonNull View view) {
    Staff staff = StaffUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        staffRepository
            .updateStaff(staff)
            .observeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onUpdateStaffSuccess, this::onUpdateStaffError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onCreate(@NonNull View view) {
    Staff staff = StaffUiModelMapper.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        staffRepository
            .createStaff(staff)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onCreateStaffSuccess, this::onCreateStaffError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onDelete(@NonNull View view) {
    String staffId = Objects.requireNonNull(uiModel.getValue()).getId();

    Disposable disposable =
        staffRepository
            .deleteStaff(staffId)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onDeleteStaffSuccess, this::onDeleteStaffError);

    disposables.add(disposable);
  }

  private void initStaffInputsListener() {
    staffOnPropertyChangedCallback =
        new Observable.OnPropertyChangedCallback() {
          @Override
          public void onPropertyChanged(Observable sender, int propertyId) {
            switch (propertyId) {
              case BR.name -> validateName();
              case BR.email -> validateEmail();
              case BR.phone -> validatePhone();
            }
          }

          private void validateName() {
            validateField(ERROR_NAME, StaffUiModel::getName, staffValidator::validateStaffName);
          }

          private void validateEmail() {
            validateField(ERROR_EMAIL, StaffUiModel::getEmail, staffValidator::validateEmail);
          }

          private void validatePhone() {
            validateField(ERROR_PHONE, StaffUiModel::getPhone, staffValidator::validatePhone);
          }
        };

    uiModel.observeForever(this::attachPropertyChangeCallback);
  }

  private void attachPropertyChangeCallback(StaffUiModel uiModel) {
    if (uiModel != null && staffOnPropertyChangedCallback != null) {
      uiModel.addOnPropertyChangedCallback(staffOnPropertyChangedCallback);
    }
  }

  private void onDeleteStaffSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.alter_delete_staff_success);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_staff_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onDeleteStaffError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error while deleting staff: %s", throwable.getMessage());
  }

  private void onCreateStaffSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      messageResId.setValue(R.string.alter_create_staff_success);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.DuplicateError) {
        messageResId.setValue(R.string.error_email_already_in_use);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onCreateStaffError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error while creating staff: %s", throwable.getMessage());
  }

  private void onUpdateStaffSuccess(Result<Staff> result) {
    if (result instanceof Result.Success<Staff>) {
      messageResId.setValue(R.string.alert_staff_update_success);
      Timber.d("Staff updated successfully");
    } else if (result instanceof Result.Failure<Staff> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_staff_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onUpdateStaffError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "Error updating staff: %s", throwable.getMessage());
  }

  private void onGetStaffSuccess(Result<Staff> result) {
    if (result instanceof Result.Success<Staff>) {
      Staff staff = ((Result.Success<Staff>) result).getData();
      if (staff == null) {
        Timber.e("Received null staff data");
        messageResId.setValue(R.string.error_unknown);
        return;
      }

      // Map the domain model to the UI model and set it
      uiModel.setValue(StaffUiModelMapper.fromDomain(staff));
    } else if (result instanceof Result.Failure<Staff> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_staff_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetStaffError(Throwable throwable) {
    Timber.e(
        throwable, "There was an error while retrieving staff's data: %s", throwable.getMessage());
    messageResId.setValue(R.string.error_unknown);
  }
}
