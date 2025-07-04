package com.optlab.banhangso.paging.staff;

import static com.optlab.banhangso.internal.utilities.Constants.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import com.optlab.banhangso.services.interfaces.StaffService;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class StaffPagingSource extends BaseStaffPagingSource {

  public StaffPagingSource(PreferencesRepository preferencesRepository, StaffService staffService) {
    super(preferencesRepository, staffService);
  }

  @NonNull @Override
  public Single<LoadResult<Integer, StaffFirebaseObject>> loadSingle(
      @NonNull LoadParams<Integer> loadParams) {
    int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

    return getStoreId()
        .flatMap(
            storeId ->
                staffService
                    .getStaffs(storeId, currentPageNumber, ITEMS_PER_PAGE)
                    .subscribeOn(Schedulers.io())
                    .map(this::mapToResult)
                    .onErrorReturn(LoadResult.Error::new));
  }
}
