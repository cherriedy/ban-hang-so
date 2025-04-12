package com.optlab.banhangso.ui.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.databinding.ListItemCategoryTagBinding;
import com.optlab.banhangso.ui.listener.OnCategorySelectListener;

public class CategoryTagSelectionAdapter
        extends ListAdapter<Category, CategoryTagSelectionAdapter.ViewHolder> {
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
                    return oldItem.getName().equals(newItem.getName());
                }
            };

    private final OnCategorySelectListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public CategoryTagSelectionAdapter(@NonNull final OnCategorySelectListener listener) {
        super(CALL_BACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemCategoryTagBinding binding =
                ListItemCategoryTagBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setSelectedPosition(int newPosition) {
        int previousPosition = selectedPosition;
        if (newPosition == previousPosition) {
            selectedPosition = RecyclerView.NO_POSITION;
            notifyItemChanged(previousPosition);
        } else {
            selectedPosition = newPosition;
            if (previousPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(previousPosition);
            }
            notifyItemChanged(newPosition);
        }
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemCategoryTagBinding binding;

        public ViewHolder(@NonNull ListItemCategoryTagBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot()
                    .setOnClickListener(
                            v -> {
                                setSelectedPosition(getBindingAdapterPosition());
                                listener.onClick(selectedPosition);
                            });
        }

        public void bind(Category category) {
            binding.setCategory(category);
            binding.executePendingBindings();
            setupAppearance();
        }

        private void setupAppearance() {
            Context context = binding.getRoot().getContext();
            int selectedColor = ContextCompat.getColor(context, R.color.color_primary);
            int unselectedColor = ContextCompat.getColor(context, R.color.color_text_title);
            Drawable selectedBg =
                    ContextCompat.getDrawable(context, R.drawable.bg_in_stock_checked);
            Drawable unselectedBg =
                    ContextCompat.getDrawable(context, R.drawable.bg_out_stock_unchecked);

            if (selectedPosition == getBindingAdapterPosition()) {
                binding.tvName.setTextColor(selectedColor);
                binding.getRoot().setBackground(selectedBg);
            } else {
                binding.tvName.setTextColor(unselectedColor);
                binding.getRoot().setBackground(unselectedBg);
            }
        }
    }
}
