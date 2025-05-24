package com.optlab.banhangso.data.repository;

import com.optlab.banhangso.data.model.app.SortOption;

public interface PreferenceRepository {
    void setSortOption(SortOption<? extends Enum<?>> sortOption, String key);

    SortOption<?> getSortOption(String key);
}
