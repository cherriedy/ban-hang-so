package com.optlab.banhangso.paging.transaction;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.application.FilterParams;
import com.optlab.banhangso.models.remote.TransactionSummaryFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.TransactionService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.util.Map;

public class TransactionFiltersPagingSource extends BaseTransactionPagingSource {

  private final FilterParams filterParams;

  public TransactionFiltersPagingSource(
      PreferencesRepository preferencesRepository,
      TransactionService transactionService,
      FilterParams filterParams) {
    super(preferencesRepository, transactionService);
    this.filterParams = filterParams;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, TransactionSummaryFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .observeOn(Schedulers.io())
        .flatMap(
            storeId -> {
              // Convert FilterParams to Map<String, Object> .
              Map<String, Object> queryParams = filterParams.toMap();
              // Adding parameters for getting transactions.
              return transactionService.getTransactions(
                  storeId, currentPageNumber, ITEMS_PER_PAGE, queryParams);
            })
        .map(this::mapToResult)
        .onErrorReturn(LoadResult.Error::new);
  }
}
