package com.optlab.banhangso.paging.transaction;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.TransactionSummaryFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.TransactionService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TransactionSearchPagingSource extends BaseTransactionPagingSource {

  @NonNull private final String query;

  public TransactionSearchPagingSource(
      PreferencesRepositoryKt preferencesRepository,
      TransactionService transactionService,
      @NonNull String query) {
    super(preferencesRepository, transactionService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, TransactionSummaryFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .observeOn(Schedulers.io())
        .flatMap(
            storeId ->
                transactionService.searchTransactions(
                    storeId, currentPageNumber, ITEMS_PER_PAGE, query))
        .map(this::mapToResult)
        .onErrorReturn(LoadResult.Error::new);
  }
}
