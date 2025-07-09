package com.optlab.banhangso.repositories.interfaces;

import androidx.annotation.NonNull;
import androidx.paging.PagingData;
import com.optlab.banhangso.models.application.Result;
import com.optlab.banhangso.models.domain.Customer;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public interface CustomerRepository extends BaseRepository {

  @NonNull Flowable<PagingData<Customer>> getCustomers();

  @NonNull Single<Result<Customer>> getCustomer(@NonNull String customerId);

  @NonNull Flowable<PagingData<Customer>> searchCustomers(@NonNull String query);

  @NonNull Single<Result<Void>> updateCustomer(@NonNull Customer customer);

  @NonNull Single<Result<Void>> deleteCustomer(@NonNull String customerId);

  @NonNull Single<Result<Void>> createCustomer(@NonNull Customer customer);
}
