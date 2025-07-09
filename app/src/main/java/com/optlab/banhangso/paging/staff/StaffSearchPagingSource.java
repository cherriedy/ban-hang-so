package com.optlab.banhangso.paging.staff;

import static com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.StaffService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StaffSearchPagingSource extends BaseStaffPagingSource {

  private final String query;

  public StaffSearchPagingSource(
      PreferencesRepository preferencesRepository, StaffService staffService, String query) {
    super(preferencesRepository, staffService);
    this.query = query;
  }

  @NonNull @Override
  public Single<LoadResult<Integer, StaffFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .observeOn(Schedulers.io())
        .flatMap(
            storeId ->
                staffService
                    .searchStaff(storeId, currentPageNumber, ITEMS_PER_PAGE, query)
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
