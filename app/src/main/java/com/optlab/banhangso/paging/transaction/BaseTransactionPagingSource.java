package com.optlab.banhangso.paging.transaction;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.TransactionSummaryFirebaseObject;
import com.optlab.banhangso.models.remote.responses.TransactionResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.TransactionService;
import java.util.List;
import timber.log.Timber;

public abstract class BaseTransactionPagingSource
    extends BasePagingSource<TransactionSummaryFirebaseObject> {

  protected final TransactionService transactionService;

  protected BaseTransactionPagingSource(
      PreferencesRepository preferencesRepository, TransactionService transactionService) {
    super(preferencesRepository);
    this.transactionService = transactionService;
  }

  @NonNull protected LoadResult<Integer, TransactionSummaryFirebaseObject> mapToResult(
      @NonNull Response<TransactionResponse.Collection> response) {
    if (response.isFailure()) {
      Throwable throwable = new ApiResponseException(response.message(), response.code());
      return new LoadResult.Error<>(throwable);
    } else {
      TransactionResponse.Collection collection = response.data();
      List<TransactionSummaryFirebaseObject> items = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (items.isEmpty()) {
        Timber.d("No items found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevPage = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextPage = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d("Loaded page %d with %d items", currentPageNumber, items.size());

      return new LoadResult.Page<>(items, prevPage, nextPage);
    }
  }
}
