package com.optlab.banhangso.features.shared.callbacks;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.R;

public abstract class BaseSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

  private final Drawable deleteIcon;
  private final ColorDrawable background;

  /**
   * Constructor for BaseSwipeToDeleteCallback, which sets up the swipe-to-delete functionality with
   * a red background and delete icon.
   *
   * @param context the context to access resources for the delete icon
   */
  protected BaseSwipeToDeleteCallback(Context context) {
    // dragDirs = 0 (no drag support), swipeDirs = ItemTouchHelper.LEFT (swipe left to delete)
    super(0, ItemTouchHelper.LEFT);
    background = new ColorDrawable(Color.RED);
    deleteIcon = ContextCompat.getDrawable(context, R.drawable.ic_delete);
  }

  @Override
  public boolean onMove(
      @NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder,
      @NonNull RecyclerView.ViewHolder target) {
    return false;
  }

  @Override
  public void onChildDraw(
      @NonNull Canvas c,
      @NonNull RecyclerView recyclerView,
      @NonNull RecyclerView.ViewHolder viewHolder,
      float dX,
      float dY,
      int actionState,
      boolean isCurrentlyActive) {
    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
      View itemView = viewHolder.itemView;
      int itemHeight = itemView.getBottom() - itemView.getTop();

      // Draw the red delete background
      background.setBounds(
          itemView.getRight() + (int) dX,
          itemView.getTop(),
          itemView.getRight(),
          itemView.getBottom());
      background.draw(c);

      // Draw the delete icon
      if (deleteIcon != null) {
        int iconMargin = (itemHeight - deleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + iconMargin;
        int iconBottom = iconTop + deleteIcon.getIntrinsicHeight();
        int iconLeft = itemView.getRight() - iconMargin - deleteIcon.getIntrinsicWidth();
        int iconRight = itemView.getRight() - iconMargin;

        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        deleteIcon.draw(c);
      }
    }
  }
}
