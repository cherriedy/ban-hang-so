package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.databinding.ListItemProductSortSelectionBinding;
import com.optlab.banhangso.ui.listener.OnProductSortSelectListener;
import com.optlab.banhangso.data.model.Product;
import com.optlab.banhangso.data.model.SortOption;

import java.util.List;

public class ProductSortSelectionAdapter extends RecyclerView.Adapter<ProductSortSelectionAdapter.ViewHolder> {

    private final List<SortOption<Product.SortField>> sortOptions;
    private final OnProductSortSelectListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public ProductSortSelectionAdapter(@NonNull final List<SortOption<Product.SortField>> sortOptions,
                                       @NonNull final OnProductSortSelectListener listener) {
        this.sortOptions = sortOptions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemProductSortSelectionBinding binding = ListItemProductSortSelectionBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(sortOptions.get(position));
    }

    @Override
    public int getItemCount() {
        return sortOptions.size();
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

            binding.getRoot().setOnClickListener(v -> {
                int newPosition = getBindingAdapterPosition();
                setCheckedPosition(newPosition);
                listener.onClick(sortOptions.get(newPosition));
            });
        }

        public void bind(@NonNull SortOption<Product.SortField> sortOption) {
            binding.setOption(sortOption);
            binding.rbSelect.setChecked(selectedPosition == getBindingAdapterPosition());
            binding.executePendingBindings();
        }
    }
}