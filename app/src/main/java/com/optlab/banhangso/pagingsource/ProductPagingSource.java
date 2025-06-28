package com.optlab.banhangso.pagingsource;

import static com.optlab.banhangso.internal.utilities.Constants.ITEMS_PER_PAGE;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingState;
import androidx.paging.rxjava3.RxPagingSource;

import com.optlab.banhangso.models.exceptions.ApiResponseException;
import com.optlab.banhangso.models.remote.ProductFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.ProductResponseObject;
import com.optlab.banhangso.models.remote.render_api.RenderResponseObject;
import com.optlab.banhangso.services.interfaces.RenderProductService;

import org.jetbrains.annotations.Contract;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@Singleton
public class ProductPagingSource extends RxPagingSource<Integer, ProductFirebaseObject> {

    /** Service interface for fetching product data from the backend */
    private final RenderProductService renderProductService;

    /**
     * Constructor for the ProductPagingSource with dependency injection
     *
     * @param size The number of items to load per page
     * @param renderProductService Service for fetching product data
     */
    @Inject
    public ProductPagingSource(RenderProductService renderProductService) {
        this.renderProductService = renderProductService;
    }

    /**
     * Core method responsible for loading a page of data. This is called by the Paging library when
     * it needs to load more data.
     *
     * @param loadParams Contains parameters for the load operation including the page key
     * @return A Single that emits a LoadResult containing the loaded data
     */
    @NonNull @Override
    public Single<LoadResult<Integer, ProductFirebaseObject>> loadSingle(
            @NonNull LoadParams<Integer> loadParams) {
        int currentPageNumber = loadParams.getKey() != null ? loadParams.getKey() : 1;

        // Call the service to fetch products for the specified page
        return renderProductService
                .getProducts(currentPageNumber, ITEMS_PER_PAGE)
                .subscribeOn(Schedulers.io())
                .map(this::toLoadResult)
                .onErrorReturn(LoadResult.Error::new);
    }

    /**
     * Transforms the API response into a Paging LoadResult
     *
     * @param renderResponseObject The response from the API containing product data
     * @return A LoadResult that can be either a Page (success) or Error (failure)
     */
    @NonNull @Contract(pure = true)
    private LoadResult<Integer, ProductFirebaseObject> toLoadResult(
            @NonNull RenderResponseObject<ProductResponseObject.ProductCollection>
                            renderResponseObject) {
        if (renderResponseObject.isError() || renderResponseObject.isFailure()) {
            Timber.e("Error loading products: %s", renderResponseObject.message());
            return new LoadResult.Error<>(
                    new ApiResponseException(
                            renderResponseObject.message(), renderResponseObject.code()));
        }

        // Process successful response, extracting the product items and pagination info
        ProductResponseObject.ProductCollection productCollection = renderResponseObject.data();
        List<ProductFirebaseObject> productFirebaseObjects = productCollection.items();

        int currentPageNumber = productCollection.page();
        int totalPageNumber = productCollection.pages();

        if (productFirebaseObjects.isEmpty()) {
            Timber.w("No products found for page %d", currentPageNumber);
            return new LoadResult.Page<>(List.of(), null, null);
        }

        // Calculate previous page number, null if we're at the first page
        Integer prevPageNumber = currentPageNumber > 1 ? currentPageNumber - 1 : null;

        // Calculate next page number, null if we're at the last page
        Integer nextPageNumber = currentPageNumber < totalPageNumber ? currentPageNumber + 1 : null;

        Timber.d(
                "Loaded page %d of %d pages with %d products",
                currentPageNumber, totalPageNumber, productFirebaseObjects.size());

        return new LoadResult.Page<>(productFirebaseObjects, prevPageNumber, nextPageNumber);
    }

    /**
     * Provides a key for refreshing the data when the user performs a refresh operation. This
     * method is called when the Paging library needs to refresh its data, typically after a
     * configuration change or when explicitly requested.
     *
     * @param pagingState Current state of the paging system including loaded pages
     * @return The key to be used for refreshing data, or null if refresh isn't possible
     */
    @Nullable @Override
    public Integer getRefreshKey(@NonNull PagingState<Integer, ProductFirebaseObject> pagingState) {
        // Try to find the page key of the closest page to the anchor position
        Integer anchorPosition = pagingState.getAnchorPosition();
        if (anchorPosition == null) {
            return null;
        }

        LoadResult.Page<Integer, ProductFirebaseObject> anchorPage =
                pagingState.closestPageToPosition(anchorPosition);
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
