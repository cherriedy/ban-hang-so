package com.optlab.banhangso.features.main.home.viewmodels;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;

import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.home.models.ReportSummaryUiModel;
import com.optlab.banhangso.features.main.home.models.mappers.ReportSummaryUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.ReportSummary;
import com.optlab.banhangso.models.domain.User;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.AuthRepository;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.repositories.interfaces.ReportRepository;

import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@HiltViewModel
public class HomeViewModel extends RxViewModel {

  private final AuthRepository authRepository;
  private final ReportRepository reportRepository;
  private final MutableLiveData<Boolean> signOutResult = new MutableLiveData<>();
  private final MutableLiveData<ReportSummaryUiModel> reportSummary = new MutableLiveData<>();

  private final LiveData<User> user;
  private final LiveData<RoleStore> store;

  @Inject
  public HomeViewModel(
      AuthRepository authRepository,
      PreferencesRepositoryKt preferencesRepositoryKt,
      ReportRepository reportRepository) {
    this.authRepository = authRepository;
    this.reportRepository = reportRepository;

    user =
        LiveDataReactiveStreams.fromPublisher(
            preferencesRepositoryKt
                .getUserRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));

    store =
        LiveDataReactiveStreams.fromPublisher(
            preferencesRepositoryKt
                .getStoreRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));
    Timber.d(
        "HomeViewModel initialized with user: %s, store: %s", user.getValue(), store.getValue());

    fetchReportSummary();
  }

  public void fetchReportSummary() {
    Disposable disposable =
        reportRepository
            .getSummary()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onGetReportSummarySuccess, this::onGetReportSummaryError);

    disposables.add(disposable);
  }

  private void onGetReportSummarySuccess(@NonNull Result<ReportSummary> result) {
    if (result instanceof Result.Success<ReportSummary> success) {
      ReportSummaryUiModel uiModel =
          ReportSummaryUiModelMapper.fromDomain(Objects.requireNonNull(success.getData()));
      reportSummary.setValue(uiModel);
    } else if (result instanceof Result.Failure<ReportSummary> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else if (appError instanceof AppError.UnknownError) {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  private void onGetReportSummaryError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(
        throwable, "There was an error while getting report summary: %s", throwable.getMessage());
  }

  @Override
  protected void onCleared() {
    disposables.clear();
    super.onCleared();
  }

  public LiveData<Boolean> getSignOutResult() {
    return signOutResult;
  }

  public LiveData<RoleStore> getStore() {
    return store;
  }

  public LiveData<User> getUser() {
    return user;
  }

  public LiveData<ReportSummaryUiModel> getReportSummary() {
    return reportSummary;
  }

  /**
   * @noinspection unused
   */
  public void onSignOut(@NonNull View view) {
    Disposable disposable =
        authRepository
            .signOut()
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onSignOutSuccess, this::onbSignOutError);

    disposables.add(disposable);
  }

  private void onSignOutSuccess() {
    signOutResult.setValue(true);
  }

  private void onbSignOutError(Throwable throwable) {
    signOutResult.setValue(false);
    Timber.e(throwable, "There was an error while signing out: %s", throwable.getMessage());
  }
}
