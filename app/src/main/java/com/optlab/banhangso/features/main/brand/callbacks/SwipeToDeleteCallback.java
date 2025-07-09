package com.optlab.banhangso.features.main.brand.callbacks;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.features.main.brand.adapters.BrandListAdapter;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.shared.callbacks.BaseSwipeToDeleteCallback;
import timber.log.Timber;

public class SwipeToDeleteCallback extends BaseSwipeToDeleteCallback {

  @NonNull private final BrandListAdapter listAdapter;
  @NonNull private final OnSwipeListener listener;

  @FunctionalInterface
  public interface OnSwipeListener {
    void onSwiped(@NonNull String brandId);
  }

  public SwipeToDeleteCallback(
      @NonNull Context context,
      @NonNull BrandListAdapter listAdapter,
      @NonNull OnSwipeListener listener) {
    super(context);
    this.listAdapter = listAdapter;
    this.listener = listener;
  }

  @Override
  public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    int position = viewHolder.getBindingAdapterPosition();
    BrandUiModel brand = listAdapter.getBrandAt(position);
    if (brand != null) {
      listener.onSwiped(brand.getId());
      Timber.d(
          "Brand with ID %s at position %d has been swiped for deletion", brand.getId(), position);
    } else {
      Timber.d("Unable to delete brand: BrandUiModel at position %d is null", position);
    }
  }
}
