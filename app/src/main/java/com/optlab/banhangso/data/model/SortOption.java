package com.optlab.banhangso.data.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

public class SortOption<T extends Enum<T>> extends BaseObservable {
    private T sortField;
    private boolean isAscending;

    public SortOption(T sortField, boolean isAscending) {
        this.sortField = sortField;
        this.isAscending = isAscending;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof SortOption<?> that) {
            return this.getDisplayName().equals(that.getDisplayName())
                    && this.isAscending == that.isAscending;
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return "SortOption{" + "sortField=" + getDisplayName() + ", isAscending=" + isAscending + '}';
    }

    @Bindable
    public T getSortField() {
        return sortField;
    }

    @Bindable
    public boolean isAscending() {
        return isAscending;
    }

    public void setSortField(T sortField) {
        this.sortField = sortField;
        notifyPropertyChanged(BR.sortField);
    }

    public void setAscending(boolean ascending) {
        isAscending = ascending;
        notifyPropertyChanged(BR.ascending);
    }

    public final String getDisplayName() {
        if (sortField instanceof Product.SortField sf) {
            return sf.getDisplayName(isAscending);
        }
        return isAscending ? (sortField.name() + " ↑") : (sortField.name() + " ↓");
    }
}
