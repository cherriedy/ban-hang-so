package com.optlab.banhangso.features.main.transaction.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemTransactionItemBinding;
import com.optlab.banhangso.features.main.transaction.models.TransactionRecordUiModel;

public class TransactionRecordListAdapter
    extends ListAdapter<TransactionRecordUiModel.Item, TransactionRecordListAdapter.ViewHolder> {

  public TransactionRecordListAdapter() {
    super(new DiffCallback());
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemTransactionItemBinding binding =
        ListItemTransactionItemBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(getItem(position));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemTransactionItemBinding binding;

    public ViewHolder(@NonNull ListItemTransactionItemBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull TransactionRecordUiModel.Item item) {
      binding.setItem(item);
      binding.executePendingBindings();
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<TransactionRecordUiModel.Item> {

    @Override
    public boolean areItemsTheSame(
        @NonNull TransactionRecordUiModel.Item oldItem,
        @NonNull TransactionRecordUiModel.Item newItem) {
      return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull TransactionRecordUiModel.Item oldItem,
        @NonNull TransactionRecordUiModel.Item newItem) {
      return oldItem.equals(newItem);
    }
  }
}
