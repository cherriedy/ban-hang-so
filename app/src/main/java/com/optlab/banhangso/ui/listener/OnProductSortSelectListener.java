package com.optlab.banhangso.ui.listener;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

public interface OnProductSortSelectListener {
    void onClick(SortOption<Product.SortField> sortOption);
}
