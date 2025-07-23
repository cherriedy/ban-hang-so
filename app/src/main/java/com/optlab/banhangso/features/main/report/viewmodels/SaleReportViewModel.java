package com.optlab.banhangso.features.main.report.viewmodels;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.github.mikephil.charting.data.BarEntry;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.report.models.RevenueChartData;
import com.optlab.banhangso.features.main.report.models.SaleReportUiModel;
import com.optlab.banhangso.features.main.report.models.TransactionChartData;
import com.optlab.banhangso.features.main.report.models.mappers.SaleReportUiModelMapper;
import com.optlab.banhangso.features.shared.viewmodels.RxViewModel;
import com.optlab.banhangso.models.application.AppError;
import com.optlab.banhangso.models.application.FilterParams;
import com.optlab.banhangso.models.application.PriceUnit;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.SaleReport;
import com.optlab.banhangso.repositories.interfaces.ReportRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;
import javax.inject.Inject;
import org.jetbrains.annotations.Contract;
import timber.log.Timber;

/**
 * @noinspection LombokGetterMayBeUsed
 */
@HiltViewModel
public class SaleReportViewModel extends RxViewModel {

  private final ReportRepository reportRepository;
  private final MutableLiveData<SaleReportUiModel> saleReport = new MutableLiveData<>();
  private final MutableLiveData<RevenueChartData> priceChartData = new MutableLiveData<>();
  private final MutableLiveData<TransactionChartData> transactionsChartData =
      new MutableLiveData<>();

  @Inject
  public SaleReportViewModel(ReportRepository reportRepository) {
    this.reportRepository = reportRepository;
    fetchSaleReport("", "");
  }

  public LiveData<SaleReportUiModel> getSaleReport() {
    return saleReport;
  }

  public LiveData<RevenueChartData> getPriceChartData() {
    return priceChartData;
  }

  public LiveData<TransactionChartData> getTransactionsChartData() {
    return transactionsChartData;
  }

  public void setFilterParams(@NonNull FilterParams filterParams) {
    fetchSaleReport(filterParams.getStartDate(), filterParams.getEndDate());
  }

  private void fetchSaleReport(@NonNull String startDate, @NonNull String endDate) {
    Disposable disposable =
        reportRepository
            .getSaleReport(startDate, endDate)
            .subscribeOn(Schedulers.io())
            .doOnSubscribe(__ -> isLoading.postValue(true))
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally(() -> isLoading.setValue(false))
            .subscribe(this::onFetchSaleReportSuccess, this::onFetchSaleReportError);

    disposables.add(disposable);
  }

  private void onFetchSaleReportError(Throwable throwable) {
    messageResId.setValue(R.string.error_unknown);
    Timber.e(throwable, "There was an error fetching the sale report: %s", throwable.getMessage());
  }

  private void onFetchSaleReportSuccess(Result<SaleReport> result) {
    if (result instanceof Result.Success<SaleReport> success) {
      SaleReportUiModel uiModel =
          SaleReportUiModelMapper.fromDomain(Objects.requireNonNull(success.getData()));
      saleReport.setValue(uiModel);
      priceChartData.setValue(buildPriceBarChart(uiModel));
      transactionsChartData.setValue(buildTransactionsBarChart(uiModel));
    } else if (result instanceof Result.Failure<SaleReport> failure) {
      AppError appError = failure.getError();
      if (appError instanceof AppError.NetServiceError) {
        messageResId.setValue(R.string.error_network);
      } else {
        messageResId.setValue(R.string.error_unknown);
      }
    }
  }

  @NonNull @Contract("_ -> new")
  private RevenueChartData buildPriceBarChart(@NonNull SaleReportUiModel uiModel) {
    List<SaleReportUiModel.RevenueByDate.Data> data = uiModel.getRevenueByDate().getData();
    List<BarEntry> barEntries = new ArrayList<>();
    List<String> xAxisLabels = new ArrayList<>();
    PriceUnit unit = uiModel.getRevenueByDate().getUnit();

    IntStream.range(0, data.size())
        .forEachOrdered(
            i -> {
              barEntries.add(new BarEntry(i, (float) data.get(i).getValue()));
              xAxisLabels.add(data.get(i).getDate());
            });
    return new RevenueChartData(barEntries, xAxisLabels, unit);
  }

  @NonNull @Contract("_ -> new")
  private TransactionChartData buildTransactionsBarChart(@NonNull SaleReportUiModel uiModel) {
    List<SaleReportUiModel.TransactionByDate.Data> data = uiModel.getTransactionByDate().getData();
    List<BarEntry> barEntries = new ArrayList<>();
    List<String> xAxisLabels = new ArrayList<>();

    // The first parameter is the index of the bar, and the second is the value.
    IntStream.range(0, data.size())
        .forEachOrdered(
            i -> {
              barEntries.add(new BarEntry(i, data.get(i).getValue()));
              xAxisLabels.add(data.get(i).getDate());
            });
    return new TransactionChartData(barEntries, xAxisLabels);
  }
}
