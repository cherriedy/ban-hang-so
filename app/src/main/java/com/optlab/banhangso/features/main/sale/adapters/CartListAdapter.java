package com.optlab.banhangso.features.main.sale.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemCartBinding;
import com.optlab.banhangso.features.main.sale.listeners.CartItemListener;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;
import com.optlab.banhangso.features.main.sale.utilities.QuantityInputUtils;
import timber.log.Timber;

public class CartListAdapter extends ListAdapter<CartUiModel.Item, CartListAdapter.ViewHolder> {

  @NonNull private final CartItemListener cartItemListener;

  public CartListAdapter(@NonNull CartItemListener cartItemListener) {
    super(new DiffCallback());
    this.cartItemListener = cartItemListener;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemCartBinding binding =
        ListItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    CartUiModel.Item uiModel = getItem(position);
    if (uiModel == null) {
      Timber.e("Item at position %d is null", position);
      return;
    }

    holder.bind(uiModel, cartItemListener);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemCartBinding binding;

    public ViewHolder(@NonNull ListItemCartBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull CartUiModel.Item item, @NonNull CartItemListener listener) {
      binding.setItem(item);
      binding.executePendingBindings();
      configureQuantityControls(item, listener);
    }

    private void configureQuantityControls(
        @NonNull CartUiModel.Item item, @NonNull CartItemListener listener) {

      binding.ibDecrease.setOnClickListener(
          v -> {
            item.decQuantity();
            listener.onQuantityChanged(item);
          });

      binding.ibIncrease.setOnClickListener(
          v -> {
            item.incQuantity();
            listener.onQuantityChanged(item);
          });

      binding.ibRemove.setOnClickListener(v -> listener.onItemRemoved(item));

      // Configure the EditText for quantity input.
      QuantityInputUtils.configureQuantityEditText(binding.etQuantity, item, listener);
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<CartUiModel.Item> {
    @Override
    public boolean areItemsTheSame(
        @NonNull CartUiModel.Item oldItem, @NonNull CartUiModel.Item newItem) {
      return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull CartUiModel.Item oldItem, @NonNull CartUiModel.Item newItem) {
      return oldItem.equals(newItem);
    }
  }
}
