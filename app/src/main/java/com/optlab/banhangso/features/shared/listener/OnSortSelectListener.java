package com.optlab.banhangso.features.shared.listener;

import com.optlab.banhangso.models.application.SortOption;

public interface OnSortSelectListener<T extends Enum<T>> {
    void onClick(SortOption<T> sortOption);
}
