package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.FilterParams;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Cart;
import com.optlab.banhangso.models.domain.TransactionRecord;
import com.optlab.banhangso.models.domain.TransactionSummary;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface TransactionRepository extends BaseRepository {
  @NonNull Flowable<PagingData<TransactionSummary>> getTransactions(@NonNull FilterParams filterParams);

  @NonNull Flowable<PagingData<TransactionSummary>> searchTransactions(@NonNull String query);

  @NonNull Single<Result<TransactionRecord>> getTransaction(@NonNull String transactionId);

  @NonNull Single<Result<TransactionRecord>> setTransaction(@NonNull Cart cart);
}
