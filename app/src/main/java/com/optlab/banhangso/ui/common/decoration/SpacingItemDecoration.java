package com.optlab.banhangso.ui.common.decoration;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final SpacingStrategy spacingStrategy;

    public SpacingItemDecoration(SpacingStrategy spacingStrategy) {
        this.spacingStrategy = spacingStrategy;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                               @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        spacingStrategy.getItemOffsets(outRect, view, parent, state);
    }
}
