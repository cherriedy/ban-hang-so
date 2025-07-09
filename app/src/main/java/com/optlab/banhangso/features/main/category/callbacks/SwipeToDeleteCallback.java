package com.optlab.banhangso.features.main.category.callbacks;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.features.main.category.adapters.CategoryListAdapter;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import com.optlab.banhangso.features.shared.callbacks.BaseSwipeToDeleteCallback;
import timber.log.Timber;

public class SwipeToDeleteCallback extends BaseSwipeToDeleteCallback {

  private final CategoryListAdapter adapter;
  private final OnSwipeListener listener;

  @FunctionalInterface
  public interface OnSwipeListener {
    void onSwiped(@NonNull String categoryId);
  }

  public SwipeToDeleteCallback(
      Context context, CategoryListAdapter adapter, OnSwipeListener listener) {
    super(context);
    this.adapter = adapter;
    this.listener = listener;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    // Get the position of the swiped item, which is necessary to identify the category
    int position = viewHolder.getBindingAdapterPosition();
    // Get the category at the swiped position, which is necessary to retrieve the ID
    CategoryUiModel category = adapter.getCategoryAt(position);
    if (category != null) {
      listener.onSwiped(category.getId());
      Timber.d(
          "Category with ID %s at position %d has been swiped for deletion",
          category.getId(), position);
    } else {
      Timber.d("Unable to delete category: CategoryUiModel at position %d is null", position);
    }
  }
}
