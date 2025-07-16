package com.optlab.banhangso.features.main.transaction.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemTransactionBinding;
import com.optlab.banhangso.features.main.transaction.models.TransactionSummaryUiModel;
import java.util.Objects;
import java.util.function.Consumer;
import timber.log.Timber;

public class TransactionSummaryListAdapter
    extends PagingDataAdapter<TransactionSummaryUiModel, TransactionSummaryListAdapter.ViewHolder> {

  @NonNull private final Consumer<String> consumer;

  public TransactionSummaryListAdapter(@NonNull Consumer<String> consumer) {
    super(new DiffCallback());
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemTransactionBinding binding =
        ListItemTransactionBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    TransactionSummaryUiModel uiModel = getItem(position);
    if (uiModel == null) {
      Timber.e("onBindViewHolder: uiModel is null at position %d", position);
      return;
    }
    holder.bind(uiModel);
    holder.binding.getRoot().setOnClickListener(v -> consumer.accept(uiModel.getId()));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemTransactionBinding binding;

    public ViewHolder(@NonNull ListItemTransactionBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull TransactionSummaryUiModel transaction) {
      binding.setTransaction(transaction);
      binding.executePendingBindings();
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<TransactionSummaryUiModel> {
    @Override
    public boolean areItemsTheSame(
        @NonNull TransactionSummaryUiModel oldItem, @NonNull TransactionSummaryUiModel newItem) {
      return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull TransactionSummaryUiModel oldItem, @NonNull TransactionSummaryUiModel newItem) {
      return oldItem.equals(newItem);
    }
  }
}
