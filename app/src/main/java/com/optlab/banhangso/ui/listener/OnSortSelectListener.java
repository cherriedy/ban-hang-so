package com.optlab.banhangso.ui.listener;

import com.optlab.banhangso.data.model.app.SortOption;

public interface OnSortSelectListener<T extends Enum<T>> {
    void onClick(SortOption<T> sortOption);
}
