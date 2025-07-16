package com.optlab.banhangso.features.main.sale.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.GridItemSaleCreateProductBinding;
import com.optlab.banhangso.databinding.GridItemSaleProductBinding;
import com.optlab.banhangso.features.main.sale.listeners.CartItemListener;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;
import com.optlab.banhangso.features.main.sale.utilities.QuantityInputUtils;
import java.util.Objects;
import timber.log.Timber;

public class SaleListAdapter extends PagingDataAdapter<CartUiModel.Item, RecyclerView.ViewHolder> {

  private static final int VIEW_TYPE_CREATE_PRODUCT = 0;
  private static final int VIEW_TYPE_SALE_PRODUCT = 1;

  @NonNull private final CartItemListener cartItemListener;

  public SaleListAdapter(@NonNull CartItemListener cartItemListener) {
    super(new DiffCallback());
    this.cartItemListener = cartItemListener;
  }

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_CREATE_PRODUCT : VIEW_TYPE_SALE_PRODUCT;
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return switch (viewType) {
      case VIEW_TYPE_CREATE_PRODUCT -> {
        GridItemSaleCreateProductBinding binding =
            GridItemSaleCreateProductBinding.inflate(inflater, parent, false);
        yield new CreateProductViewHolder(binding);
      }
      case VIEW_TYPE_SALE_PRODUCT -> {
        GridItemSaleProductBinding binding =
            GridItemSaleProductBinding.inflate(inflater, parent, false);
        yield new SellProductViewHolder(binding);
      }
      default -> throw new IllegalStateException("Unexpected value: " + viewType);
    };
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
    if (holder instanceof SellProductViewHolder sellProductViewHolder) {
      CartUiModel.Item uiModel = getItem(position - 1);
      if (uiModel != null) {
        sellProductViewHolder.bind(uiModel, cartItemListener);
      } else {
        Timber.e("CartUiModel.Item is null at position: %d", position);
      }
    }
  }

  @Override
  public int getItemCount() {
    // Shift by 1 to account for the create product item
    return super.getItemCount() + 1;
  }

  public static class CreateProductViewHolder extends RecyclerView.ViewHolder {
    public CreateProductViewHolder(@NonNull GridItemSaleCreateProductBinding binding) {
      super(binding.getRoot());
    }
  }

  public static class SellProductViewHolder extends RecyclerView.ViewHolder {
    private final GridItemSaleProductBinding binding;

    public SellProductViewHolder(@NonNull GridItemSaleProductBinding binding) {
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

      // Configure the EditText for quantity input.
      QuantityInputUtils.configureQuantityEditText(binding.etQuantity, item, listener);
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<CartUiModel.Item> {
    @Override
    public boolean areItemsTheSame(
        @NonNull CartUiModel.Item oldItem, @NonNull CartUiModel.Item newItem) {
      return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull CartUiModel.Item oldItem, @NonNull CartUiModel.Item newItem) {
      return oldItem.equals(newItem);
    }
  }
}
