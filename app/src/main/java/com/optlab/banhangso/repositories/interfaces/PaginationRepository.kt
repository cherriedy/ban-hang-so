package com.optlab.banhangso.repositories.interfaces

import androidx.paging.PagingConfig
import com.optlab.banhangso.internal.Config.ITEMS_PER_PAGE

interface PaginationRepository {
    /**
     * Provides the configuration for pagination.
     * This configuration includes:
     *  - `pageSize`: The number of items to load per page.
     *  - `prefetchDistance`: The number of items to prefetch before the user scrolls.
     *  - `enablePlaceholders`: Whether to enable placeholders for items that are not yet loaded.
     *  - `initialLoadSize`: The initial number of items to load when the pagination starts.
     *
     */
    val pagingConfig: PagingConfig
        get() =
            PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                prefetchDistance = ITEMS_PER_PAGE,
                enablePlaceholders = false,
                initialLoadSize = ITEMS_PER_PAGE * 3,
            )
}
