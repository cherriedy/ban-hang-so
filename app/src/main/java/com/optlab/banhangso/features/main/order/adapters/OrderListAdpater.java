package com.optlab.banhangso.features.main.order.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.GridItemOrderBinding;
import com.optlab.banhangso.databinding.GridItemOrderCreateProductBinding;

public class OrderListAdpater extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private static final int VIEW_TYPE_CREATE_PRODUCT = 0;
  private static final int VIEW_TYPE_ORDER_PRODUCT = 1;

  @Override
  public int getItemViewType(int position) {
    return position == 0 ? VIEW_TYPE_CREATE_PRODUCT : VIEW_TYPE_ORDER_PRODUCT;
  }

  @NonNull @Override
  public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    return switch (viewType) {
      case VIEW_TYPE_CREATE_PRODUCT -> {
        GridItemOrderBinding binding = GridItemOrderBinding.inflate(inflater, parent, false);
        yield new OrderProductViewHolder(binding);
      }
      case VIEW_TYPE_ORDER_PRODUCT -> {
        GridItemOrderCreateProductBinding binding =
            GridItemOrderCreateProductBinding.inflate(inflater, parent, false);
        yield new CreateProductViewHolder(binding);
      }
      default -> throw new IllegalStateException("Unexpected value: " + viewType);
    };
  }

  @Override
  public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
     // NOTE: Shift by 1 to ignore the first item
  }

  @Override
  public int getItemCount() {
    return 0; // Plus 1 to ignore the default item
  }

  public static class OrderProductViewHolder extends RecyclerView.ViewHolder {
    private final GridItemOrderBinding binding;

    public OrderProductViewHolder(@NonNull GridItemOrderBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }

  public static class CreateProductViewHolder extends RecyclerView.ViewHolder {

    private final GridItemOrderCreateProductBinding binding;

    public CreateProductViewHolder(@NonNull GridItemOrderCreateProductBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }
  }
}
