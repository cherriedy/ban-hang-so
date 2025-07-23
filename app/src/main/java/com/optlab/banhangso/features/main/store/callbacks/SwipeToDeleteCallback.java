package com.optlab.banhangso.features.main.store.callbacks;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.features.main.store.adapters.RoleStoreListAdapter;
import com.optlab.banhangso.features.shared.callbacks.BaseSwipeToDeleteCallback;
import timber.log.Timber;

public class SwipeToDeleteCallback extends BaseSwipeToDeleteCallback {

  @FunctionalInterface
  public interface OnSwipeListener {
    void onSwiped(@NonNull String storeId);
  }

  private final RoleStoreListAdapter listAdapter;
  private final OnSwipeListener listener;

  public SwipeToDeleteCallback(
      Context context, RoleStoreListAdapter listAdapter, OnSwipeListener listener) {
    super(context);
    this.listAdapter = listAdapter;
    this.listener = listener;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    // Get the position of the swiped item.
    int position = viewHolder.getBindingAdapterPosition();
    // Get the store at the swiped position.
    String storeId = listAdapter.getStoreAt(position).getId();
    if (storeId != null) {
      Timber.d("Store with ID %s at position %d has been swiped for deletion", storeId, position);
      listener.onSwiped(storeId);
    } else {
      Timber.d("Unable to delete store: Store ID at position %d is null", position);
    }
  }
}
