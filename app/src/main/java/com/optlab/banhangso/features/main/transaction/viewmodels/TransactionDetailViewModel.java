package com.optlab.banhangso.features.main.transaction.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel;
import com.optlab.banhangso.features.main.transaction.models.mappers.TransactionRecordUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.TransactionRecord;
import com.optlab.banhangso.repositories.interfaces.TransactionRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Objects;
import javax.inject.Inject;
import timber.log.Timber;

@HiltViewModel
public class TransactionDetailViewModel extends RxViewModel {

  private final TransactionRepository transactionRepository;
  private final MutableLiveData<TransactionRecordUiModel> transaction = new MutableLiveData<>();

  @Inject
  public TransactionDetailViewModel(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public void getTransactionById(@NonNull String id) {
    Disposable disposable =
        transactionRepository
            .getTransaction(id)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onGetTransactionSuccess, this::onGetTransactionError);

    disposables.add(disposable);
  }

  public LiveData<TransactionRecordUiModel> getTransaction() {
    return transaction;
  }

  private void onGetTransactionSuccess(Result<TransactionRecord> result) {
    if (result instanceof Result.Success<TransactionRecord> success) {
      transaction.setValue(
          TransactionRecordUiModelMapper.fromDomain(Objects.requireNonNull(success.getData())));
    } else if (result instanceof Result.Failure<TransactionRecord> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.NotFoundError) {
        messageResId.setValue(R.string.error_transaction_not_found);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetTransactionError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(
        throwable, "There was an error getting transaction details: %s", throwable.getMessage());
  }
}
