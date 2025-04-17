package com.optlab.banhangso.ui.listener;

import com.optlab.banhangso.data.model.SortOption;

public interface OnSortSelectListener<T extends Enum<T>> {
    void onClick(SortOption<T> sortOption);
}
