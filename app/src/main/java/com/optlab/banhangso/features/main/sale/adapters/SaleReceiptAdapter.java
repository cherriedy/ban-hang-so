package com.optlab.banhangso.features.main.sale.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemSaleReceiptBinding;
import com.optlab.banhangso.features.main.sale.models.ReceiptUiModel;

public class SaleReceiptAdapter
    extends ListAdapter<ReceiptUiModel.Item, SaleReceiptAdapter.ViewHolder> {

  public SaleReceiptAdapter() {
    super(new DiffCallback());
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemSaleReceiptBinding binding =
        ListItemSaleReceiptBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(getItem(position));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ListItemSaleReceiptBinding binding;

    public ViewHolder(@NonNull ListItemSaleReceiptBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull ReceiptUiModel.Item item) {
      binding.setItem(item);
      binding.executePendingBindings();
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<ReceiptUiModel.Item> {
    @Override
    public boolean areItemsTheSame(
        @NonNull ReceiptUiModel.Item oldItem, @NonNull ReceiptUiModel.Item newItem) {
      return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull ReceiptUiModel.Item oldItem, @NonNull ReceiptUiModel.Item newItem) {
      return oldItem.equals(newItem);
    }
  }
}
