package com.optlab.banhangso.ui.base.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.databinding.ListItemStoreBinding;
import com.optlab.banhangso.domain.model.Store;

import java.util.function.Consumer;

public class StoreListAdapter extends ListAdapter<Store, StoreListAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Store> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Store oldItem, @NonNull Store newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final Consumer<Store> onStoreClicked;

    public StoreListAdapter(Consumer<Store> onStoreClicked) {
        super(CALL_BACK);
        this.onStoreClicked = onStoreClicked;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemStoreBinding binding =
                ListItemStoreBinding.inflate(
                        LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
        holder.itemView.setOnClickListener(v -> onStoreClicked.accept(getItem(position)));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemStoreBinding binding;

        public ViewHolder(ListItemStoreBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Store store) {
            binding.setStore(store);
            binding.executePendingBindings();
        }
    }
}
