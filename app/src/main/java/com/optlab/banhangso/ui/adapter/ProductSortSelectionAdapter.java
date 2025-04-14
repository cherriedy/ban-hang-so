package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.databinding.ListItemProductSortSelectionBinding;
import com.optlab.banhangso.ui.listener.OnProductSortSelectListener;

public class ProductSortSelectionAdapter
        extends ListAdapter<SortOption<Product.SortField>, ProductSortSelectionAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<SortOption<Product.SortField>> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull SortOption<Product.SortField> oldItem,
                        @NonNull SortOption<Product.SortField> newItem) {
                    return oldItem.getDisplayName().equals(newItem.getDisplayName());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull SortOption<Product.SortField> oldItem,
                        @NonNull SortOption<Product.SortField> newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final OnProductSortSelectListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ProductSortSelectionAdapter(@NonNull OnProductSortSelectListener listener) {
        super(CALL_BACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemProductSortSelectionBinding binding =
                ListItemProductSortSelectionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setCheckedPosition(int newPosition) {
        if (selectedPosition == newPosition) return;

        int previousPosition = selectedPosition;
        if (selectedPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition);
        }

        selectedPosition = newPosition;
        notifyItemChanged(newPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemProductSortSelectionBinding binding;

        public ViewHolder(@NonNull ListItemProductSortSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot()
                    .setOnClickListener(
                            v -> {
                                setCheckedPosition(getBindingAdapterPosition());
                                listener.onClick(getItem(selectedPosition));
                            });
        }

        public void bind(@NonNull SortOption<Product.SortField> sortOption) {
            binding.setOption(sortOption);
            binding.rbSelect.setChecked(selectedPosition == getBindingAdapterPosition());
            binding.executePendingBindings();
        }
    }
}
