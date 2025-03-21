package com.optlab.banhangso.ui.common.decoration;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.EnumSet;
import java.util.Objects;

public class LinearSpacingStrategy implements SpacingStrategy {
    private final int spacing;
    private final EnumSet<Direction> directions;

    public enum Direction {LEFT, TOP, RIGHT, BOTTOM}

    public LinearSpacingStrategy(Context context, int spacing, EnumSet<Direction> directions) {
        this.spacing = dpToPx(context, spacing);
        this.directions = directions;
    }

    public LinearSpacingStrategy(Context context, int spacing) {
        this.spacing = dpToPx(context, spacing);
        this.directions = EnumSet.of(Direction.LEFT, Direction.RIGHT, Direction.TOP, Direction.BOTTOM);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        LinearLayoutManager llm = (LinearLayoutManager) parent.getLayoutManager();

        if (Objects.requireNonNull(llm).getOrientation() == RecyclerView.VERTICAL) {
            if (directions.contains(Direction.TOP) && position == 0) {
                outRect.top = spacing;
            }
            if (directions.contains(Direction.BOTTOM)) {
                outRect.bottom = spacing;
            }
            if (directions.contains(Direction.LEFT)) {
                outRect.left = spacing;
            }
            if (directions.contains(Direction.RIGHT)) {
                outRect.right = spacing;
            }
        } else { // HORIZONTAL orientation
            if (directions.contains(Direction.LEFT) && position == 0) {
                outRect.left = spacing;
            }
            if (directions.contains(Direction.RIGHT)) {
                outRect.right = spacing;
            }
            if (directions.contains(Direction.TOP)) {
                outRect.top = spacing;
            }
            if (directions.contains(Direction.BOTTOM)) {
                outRect.bottom = spacing;
            }
        }
    }
}