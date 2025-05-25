package com.optlab.banhangso.ui.base.listener;

import com.optlab.banhangso.domain.util.SortOption;

public interface OnSortSelectListener<T extends Enum<T>> {
    void onClick(SortOption<T> sortOption);
}
