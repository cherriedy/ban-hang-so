package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.databinding.ListItemCategoryBinding;
import com.optlab.banhangso.ui.listener.OnCategoryClickListener;

public class CategoryListAdapter extends ListAdapter<Category, CategoryListAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<Category> CALL_BACK =
            new DiffUtil.ItemCallback<>() {
                @Override
                public boolean areItemsTheSame(
                        @NonNull Category oldItem, @NonNull Category newItem) {
                    return oldItem.getId().equals(newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(
                        @NonNull Category oldItem, @NonNull Category newItem) {
                    return oldItem.equals(newItem);
                }
            };

    private final OnCategoryClickListener listener;

    public CategoryListAdapter(@NonNull final OnCategoryClickListener listener) {
        super(CALL_BACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemCategoryBinding binding = ListItemCategoryBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ListItemCategoryBinding binding;

        public ViewHolder(@NonNull ListItemCategoryBinding binding) {
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

        public void bind(@NonNull Category category) {
            binding.setCategory(category);
            binding.executePendingBindings();
        }
    }
}
