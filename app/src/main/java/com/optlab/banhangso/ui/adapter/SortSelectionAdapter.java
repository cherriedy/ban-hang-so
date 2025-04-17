package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.SortOption;
import com.optlab.banhangso.databinding.ListItemSortSelectionBinding;
import com.optlab.banhangso.ui.listener.OnSortSelectListener;

public class SortSelectionAdapter<T extends Enum<T>>
        extends ListAdapter<SortOption<T>, SortSelectionAdapter<T>.ViewHolder> {
    private final OnSortSelectListener<T> listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public SortSelectionAdapter(@NonNull OnSortSelectListener<T> listener) {
        super(
                new DiffUtil.ItemCallback<>() {
                    @Override
                    public boolean areItemsTheSame(
                            @NonNull SortOption<T> oldItem, @NonNull SortOption<T> newItem) {
                        return oldItem.getDisplayName().equals(newItem.getDisplayName());
                    }

                    @Override
                    public boolean areContentsTheSame(
                            @NonNull SortOption<T> oldItem, @NonNull SortOption<T> newItem) {
                        return oldItem.equals(newItem);
                    }
                });
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemSortSelectionBinding binding =
                ListItemSortSelectionBinding.inflate(inflater, parent, false);
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
        private final ListItemSortSelectionBinding binding;

        public ViewHolder(@NonNull ListItemSortSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot()
                    .setOnClickListener(
                            v -> {
                                setCheckedPosition(getBindingAdapterPosition());
                                listener.onClick(getItem(selectedPosition));
                            });
        }

        public void bind(@NonNull SortOption<T> sortOption) {
            binding.setOption(sortOption);
            binding.rbSelect.setChecked(selectedPosition == getBindingAdapterPosition());
            binding.executePendingBindings();
        }
    }
}
