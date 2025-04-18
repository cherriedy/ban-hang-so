package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.databinding.ListItemBrandBinding;
import com.optlab.banhangso.ui.listener.OnBrandClickListener;

public class BrandListAdapter extends ListAdapter<Brand, BrandListAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Brand> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(@NonNull Brand oldItem, @NonNull Brand newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Brand oldItem, @NonNull Brand newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final OnBrandClickListener listener;

    public BrandListAdapter(@NonNull final OnBrandClickListener listener) {
        super(CALL_BACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemBrandBinding binding = ListItemBrandBinding.inflate(inflater, null, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemBrandBinding binding;

        public ViewHolder(@NonNull ListItemBrandBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot()
                    .setOnClickListener(
                            v -> {
                                int position = getBindingAdapterPosition();
                                if (position != RecyclerView.NO_POSITION) {
                                    listener.onClick(getItem(position).getId());
                                }
                            });
        }

        public void bind(@NonNull Brand brand) {
            binding.setBrand(brand);
            binding.executePendingBindings();
        }
    }
}
