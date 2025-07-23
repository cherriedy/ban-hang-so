package com.optlab.banhangso.paging.customer;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.CustomerService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomerSearchPagingSource extends BaseCustomerPagingSource {

  private final String query;

  public CustomerSearchPagingSource(
      PreferencesRepositoryKt preferencesRepository,
      CustomerService customerService,
      String query) {
    super(preferencesRepository, customerService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, CustomerFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .observeOn(Schedulers.io())
        .flatMap(
            storeId ->
                customerService
                    .searchCustomers(storeId, currentPageNumber, ITEMS_PER_PAGE, query)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
