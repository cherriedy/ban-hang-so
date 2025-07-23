package com.optlab.banhangso.paging.staff;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.StaffFirebaseObject;
import com.optlab.banhangso.models.remote.responses.StaffResponse;
import com.optlab.banhangso.models.remote.responses.base.Response;
import com.optlab.banhangso.paging.BasePagingSource;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepositoryKt;
import com.optlab.banhangso.services.interfaces.StaffService;
import java.util.List;
import org.jetbrains.annotations.Contract;
import timber.log.Timber;

public abstract class BaseStaffPagingSource extends BasePagingSource<StaffFirebaseObject> {

  protected final StaffService staffService;
  protected final PreferencesRepositoryKt preferencesRepositoryKt;

  protected BaseStaffPagingSource(
          PreferencesRepositoryKt preferencesRepositoryKt, StaffService staffService) {
    super(preferencesRepositoryKt);
    this.staffService = staffService;
    this.preferencesRepositoryKt = preferencesRepositoryKt;
  }

  @NonNull @Contract("_ -> new")
  protected LoadResult<Integer, StaffFirebaseObject> mapToResult(
      @NonNull Response<StaffResponse.Collection> staffCollectionResponse) {
    if (staffCollectionResponse.isError()) {
      Throwable throwable =
          new ApiResponseException(
              staffCollectionResponse.message(), staffCollectionResponse.code());
      return new LoadResult.Error<>(throwable);
    } else {
      StaffResponse.Collection collection = staffCollectionResponse.data();
      List<StaffFirebaseObject> staffFirebaseObjects = collection.getItems();

      int currentPageNumber = collection.getPage();
      int totalPageNumber = collection.getPages();

      if (staffFirebaseObjects.isEmpty()) {
        Timber.w("No staff found for page %d", collection.getPage());
        return new LoadResult.Page<>(List.of(), null, null);
      }

      Integer prevPageNumber = currentPageNumber > 1 ? currentPageNumber - 1 : null;
      Integer nextPageNumber = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

      Timber.d(
          "Loaded page %d of %d pages with %d staffs",
          currentPageNumber, totalPageNumber, staffFirebaseObjects.size());

      return new LoadResult.Page<>(staffFirebaseObjects, prevPageNumber, nextPageNumber);
    }
  }
}
