package com.optlab.banhangso.paging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;
import com.optlab.banhangso.models.domain.store.RoleStore;
import com.optlab.banhangso.repositories.interfaces.PreferencesRepository;
import io.reactivex.rxjava3.core.Single;
import java.util.Objects;

/**
 * Base class for implementing a PagingSource that handles common functionality such as retrieving
 * the store ID from preferences. This class extends RxPagingSource to provide a reactive way of
 * loading data in pages.
 *
 * @param <V> The type of data being paged, typically a model class representing the items to be
 *     displayed in a list or grid.
 */
public abstract class BasePagingSource<V> extends RxPagingSource<Integer, V> {

  private final PreferencesRepository preferencesRepository;

  protected BasePagingSource(PreferencesRepository preferencesRepository) {
    this.preferencesRepository = preferencesRepository;
  }

  protected Single<String> getStoreId() {
    return preferencesRepository
        .getStore()
        .defaultIfEmpty(RoleStore.empty())
        .flatMap(
            store -> {
              if (store == null || store.isEmpty()) {
                return Single.error(new IllegalStateException("There is no store available"));
              }
              return Single.just(Objects.requireNonNull(store.getId()));
            });
  }

  /**
   * Provides a key for refreshing the data when the user performs a refresh operation. This method
   * is called when the Paging library needs to refresh its data, typically after a configuration
   * change or when explicitly requested.
   *
   * @param pagingState Current state of the paging system including loaded pages
   * @return The key to be used for refreshing data, or null if refresh isn't possible
   */
  @Nullable @Override
  public Integer getRefreshKey(@NonNull PagingState<Integer, V> pagingState) {
    // Try to find the page key of the closest page to the anchor position
    Integer anchorPosition = pagingState.getAnchorPosition();
    if (anchorPosition == null) {
      return null;
    }

    LoadResult.Page<Integer, V> anchorPage = pagingState.closestPageToPosition(anchorPosition);
    if (anchorPage == null) {
      return null;
    }

    Integer prevKey = anchorPage.getPrevKey();
    Integer nextKey = anchorPage.getNextKey();

    // If we're in the middle of the list, return the page that's in the middle of prev and next
    if (prevKey != null && nextKey != null) {
      return Math.min(prevKey + 1, nextKey - 1);
    }

    // If we only have a next key, we're at the start, return 1
    if (prevKey == null && nextKey != null) {
      return 1;
    }

    // If we only have a prev key, return that as the refresh point
    if (prevKey != null) {
      return prevKey + 1;
    }

    // If we have no keys, start from page 1
    return 1;
  }
}
