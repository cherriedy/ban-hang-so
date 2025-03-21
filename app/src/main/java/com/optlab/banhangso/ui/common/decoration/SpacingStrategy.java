package com.optlab.banhangso.ui.common.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface SpacingStrategy {
    void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state);

    default int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
