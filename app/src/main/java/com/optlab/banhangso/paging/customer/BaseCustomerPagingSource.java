package com.optlab.banhangso.paging.customer;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.CustomerFirebaseObject;
import com.optlab.banhangso.models.remote.responses.CustomerResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.CustomerService;
import java.util.List;
import org.jetbrains.annotations.Contract;
import timber.log.Timber;

public abstract class BaseCustomerPagingSource extends BasePagingSource<CustomerFirebaseObject> {

  protected final CustomerService customerService;

  public BaseCustomerPagingSource(
      PreferencesRepository preferencesRepository, CustomerService customerService) {
    super(preferencesRepository);
    this.customerService = customerService;
  }

  @NonNull @Contract("_ -> new")
  protected LoadResult<Integer, CustomerFirebaseObject> mapToResult(
      @NonNull Response<CustomerResponse.Collection> customerCollectionResponse) {
    if (customerCollectionResponse.isFailure()) {
      Throwable throwable =
          new ApiResponseException(
              customerCollectionResponse.message(), customerCollectionResponse.code());
      return new LoadResult.Error<>(throwable);
    } else {
      CustomerResponse.Collection collection = customerCollectionResponse.data();
      List<CustomerFirebaseObject> items = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (items == null || items.isEmpty()) {
        Timber.w("No customers found for page %d", currentPageNumber);
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevKey = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextKey = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d("Loaded page %d with %d items", currentPageNumber, items.size());

      return new LoadResult.Page<>(items, prevKey, nextKey);
    }
  }
}
