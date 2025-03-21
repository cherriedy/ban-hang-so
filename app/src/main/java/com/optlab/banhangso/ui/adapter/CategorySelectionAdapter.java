package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.databinding.ListItemCategorySelectionBinding;
import com.optlab.banhangso.ui.listener.OnCategorySelectListener;
import com.optlab.banhangso.data.model.Category;

public class CategorySelectionAdapter extends ListAdapter<Category, CategorySelectionAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Category> DIFF_CALLBACK = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Category oldItem, @NonNull Category newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };
    private final OnCategorySelectListener listener;
    private int checkedPosition = RecyclerView.NO_POSITION;

    public CategorySelectionAdapter(@NonNull final OnCategorySelectListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemCategorySelectionBinding binding = ListItemCategorySelectionBinding.inflate(
                inflater, parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setCheckedPosition(int newPosition) {
        // Only update if and on if the new position is different.
        if (newPosition == checkedPosition) {
            return;
        }

        // Save the previous position to reset view.
        int previousPosition = checkedPosition;

        // Set the new position to checked position.
        checkedPosition = newPosition;

        // If the previous position is valid, triggering rendering view again.
        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition);
        }
        // Notify the new position is set to bind data to view.
        notifyItemChanged(newPosition);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final private ListItemCategorySelectionBinding binding;

        public ViewHolder(ListItemCategorySelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int currentPosition = getBindingAdapterPosition();
                setCheckedPosition(currentPosition);
                listener.onClick(currentPosition);
            });
        }

        public void bind(@NonNull final Category category) {
            binding.setCategory(category);
            binding.rbSelect.setChecked(checkedPosition == getBindingAdapterPosition());
            binding.executePendingBindings();
        }
    }
}
