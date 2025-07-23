package com.optlab.banhangso.features.main.store.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemStoreBinding;
import com.optlab.banhangso.features.main.store.models.RoleStoreUiModel;
import java.util.function.Consumer;

public class RoleStoreListAdapter
    extends PagingDataAdapter<RoleStoreUiModel, RoleStoreListAdapter.ViewHolder> {

  @NonNull private final Consumer<RoleStoreUiModel> consumer;

  public RoleStoreListAdapter(@NonNull Consumer<RoleStoreUiModel> consumer) {
    super(new DiffCallback());
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemStoreBinding binding =
        ListItemStoreBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(getItem(position));
    holder.itemView.setOnClickListener(v -> consumer.accept(getItem(position)));
  }

  public RoleStoreUiModel getStoreAt(int position) {
    return getItem(position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemStoreBinding binding;

    public ViewHolder(@NonNull ListItemStoreBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(RoleStoreUiModel store) {
      binding.setStore(store);
      binding.executePendingBindings();
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<RoleStoreUiModel> {
    @Override
    public boolean areItemsTheSame(
        @NonNull RoleStoreUiModel oldItem, @NonNull RoleStoreUiModel newItem) {
      return oldItem.getId().equals(newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(
        @NonNull RoleStoreUiModel oldItem, @NonNull RoleStoreUiModel newItem) {
      return oldItem.equals(newItem);
    }
  }
}
