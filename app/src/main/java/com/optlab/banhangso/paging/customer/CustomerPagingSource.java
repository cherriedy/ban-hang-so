package com.optlab.banhangso.paging.customer;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.CustomerService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomerPagingSource extends BaseCustomerPagingSource {

  public CustomerPagingSource(
      PreferencesRepository preferencesRepository, CustomerService customerService) {
    super(preferencesRepository, customerService);
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
                    .getCustomers(storeId, currentPageNumber, ITEMS_PER_PAGE)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
