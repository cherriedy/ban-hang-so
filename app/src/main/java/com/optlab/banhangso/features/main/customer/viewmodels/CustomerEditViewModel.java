package com.optlab.banhangso.features.main.customer.viewmodels;

import static com.optlab.banhangso.features.main.customer.Constants.ERROR_EMAIL;
import static com.optlab.banhangso.features.main.customer.Constants.ERROR_NAME;
import static com.optlab.banhangso.features.main.customer.Constants.ERROR_PHONE;

import android.view.View;
import androidx.annotation.NonNull;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.customer.CustomerValidator;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import com.optlab.banhangso.features.main.customer.models.mappers.CustomerUiModelMappers;
import com.optlab.banhangso.features.shared.viewmodels.UiViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Customer;
import com.optlab.banhangso.repositories.interfaces.CustomerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Objects;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class CustomerEditViewModel extends UiViewModel<CustomerUiModel> {

  private final CustomerRepository customerRepository;
  private final CustomerValidator customerValidator;
  private final MutableLiveData<Boolean> isEditing = new MutableLiveData<>();
  private final MutableLiveData<Boolean> canSubmit = new MutableLiveData<>();
  private final MutableLiveData<Boolean> operationCompleted = new MutableLiveData<>();

  private Observable.OnPropertyChangedCallback customerOnPropertyChangedCallback;

  @Inject
  public CustomerEditViewModel(
      CustomerRepository customerRepository, CustomerValidator customerValidator) {
    this.customerRepository = customerRepository;
    this.customerValidator = customerValidator;

    if (Boolean.TRUE.equals(isEditing.getValue())) {
      canSubmit.setValue(true);
    }

    initCustomerInputsListener();
  }

  @Override
  protected void onCleared() {
    customerOnPropertyChangedCallback = null;
    super.onCleared();
  }

  @Override
  protected void onValidationComplete() {
    canSubmit.setValue(errors.isEmpty());
  }

  private void initCustomerInputsListener() {
    customerOnPropertyChangedCallback =
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
            validateField(ERROR_NAME, CustomerUiModel::getName, customerValidator::validateName);
          }

          private void validateEmail() {
            validateField(ERROR_EMAIL, CustomerUiModel::getEmail, customerValidator::validateEmail);
          }

          private void validatePhone() {
            validateField(ERROR_PHONE, CustomerUiModel::getPhone, customerValidator::validatePhone);
          }
        };

    uiModel.observeForever(this::attachPropertyChangeCallback);
  }

  private void attachPropertyChangeCallback(CustomerUiModel customer) {
    if (customer != null && customerOnPropertyChangedCallback != null) {
      customer.addOnPropertyChangedCallback(customerOnPropertyChangedCallback);
    }
  }

  @NonNull public LiveData<Boolean> isEditing() {
    return isEditing;
  }

  public void setIsEditing(boolean isEditing) {
    this.isEditing.setValue(isEditing);
  }

  @NonNull public LiveData<CustomerUiModel> getCustomer() {
    return uiModel;
  }

  public void setDob(@NonNull String dob) {
    CustomerUiModel currentCustomer = uiModel.getValue();
    Objects.requireNonNull(currentCustomer).setDob(dob);
  }

  @NonNull public LiveData<Boolean> canSubmit() {
    return canSubmit;
  }

  @NonNull public LiveData<Boolean> getOperationCompleted() {
    return operationCompleted;
  }

  public void loadCustomerById(@NonNull String customerId) {
    CustomerUiModel currentCustomer = uiModel.getValue();
    if (currentCustomer == null) {
      if (customerId.isBlank()) {
        uiModel.setValue(new CustomerUiModel());
      } else {
        Disposable disposable =
            customerRepository
                .getCustomer(customerId)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(__ -> isLoading.postValue(true))
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(() -> isLoading.setValue(false))
                .subscribe(this::onGetCustomerSuccess, this::onGetCustomerError);

        disposables.add(disposable);
      }
    }
  }

  /**
   * @noinspection unused
   */
  public void onUpdate(@NonNull View view) {
    Customer customer = CustomerUiModelMappers.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        customerRepository
            .updateCustomer(customer)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onUpdateCustomerSuccess, this::onUpdateCustomerError);

    disposables.add(disposable);
  }

  public void onDelete() {
    String customerId = Objects.requireNonNull(uiModel.getValue()).getId();

    Disposable disposable =
        customerRepository
            .deleteCustomer(customerId)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onDeleteCustomerSuccess, this::onDeleteCustomerError);

    disposables.add(disposable);
  }

  /**
   * @noinspection unused
   */
  public void onCreate(@NonNull View view) {
    Customer customer = CustomerUiModelMappers.toDomain(Objects.requireNonNull(uiModel.getValue()));

    Disposable disposable =
        customerRepository
            .createCustomer(customer)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onCreateCustomerSuccess, this::onCreateCustomerError);

    disposables.add(disposable);
  }

  private void onCreateCustomerSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      operationCompleted.setValue(true);
      messageResId.setValue(R.string.alter_customer_create_success);
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onCreateCustomerError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error creating customer data: %s", throwable.getMessage());
  }

  private void onDeleteCustomerSuccess(Result<Void> result) {
    if (result instanceof Result.Success<Void>) {
      operationCompleted.setValue(true);
      Timber.d("Customer deleted successfully.");
      messageResId.setValue(R.string.alter_customer_delete_success);
    } else if (result instanceof Result.Failure<Void> failure) {
      Timber.d("Customer deletion failed.");
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_customer_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onDeleteCustomerError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error deleting customer data: %s", throwable.getMessage());
  }

  private void onUpdateCustomerError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error updating customer data: %s", throwable.getMessage());
  }

  private void onUpdateCustomerSuccess(Result<Void> result) {
    if (result instanceof Result.Success) {
      messageResId.setValue(R.string.alter_update_customer_success);
      operationCompleted.setValue(true);
      Timber.d("Customer updated successfully.");
    } else if (result instanceof Result.Failure<Void> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_customer_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetCustomerSuccess(Result<Customer> result) {
    if (result instanceof Result.Success<Customer> success) {
      Customer customer = success.getData();
      if (customer == null) {
        messageResId.setValue(R.string.error_unknown);
        Timber.e("Customer not found for the given ID.");
        return;
      }
      CustomerUiModel uiModelData = CustomerUiModelMappers.fromDomain(customer);
      uiModel.setValue(uiModelData);
    } else if (result instanceof Result.Failure<Customer> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_customer_not_found);
      } else if (appError instanceof AppError.ForbiddenError) {
        messageResId.setValue(R.string.error_forbidden);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetCustomerError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error getting customer data: %s", throwable.getMessage());
  }
}
