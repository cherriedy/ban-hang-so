package com.optlab.banhangso.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.databinding.ListItemBrandSelectionBinding;
import com.optlab.banhangso.ui.listener.OnBrandSelectListener;
import com.optlab.banhangso.data.model.Brand;

public class BrandSelectionAdapter extends ListAdapter<Brand, BrandSelectionAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Brand> DIFF_CALLBACK
            = new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(@NonNull Brand oldItem, @NonNull Brand newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Brand oldItem, @NonNull Brand newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    };

    private final OnBrandSelectListener listener;
    private int checkedPosition = RecyclerView.NO_POSITION;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ListItemBrandSelectionBinding binding = ListItemBrandSelectionBinding.inflate(
                inflater, parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setCheckedPosition(int newPosition) {
        if (newPosition == checkedPosition) {
            return;
        }

        int previousPosition = checkedPosition;

        checkedPosition = newPosition;

        if (previousPosition != RecyclerView.NO_POSITION) {
            notifyItemChanged(previousPosition);
        }
        notifyItemChanged(checkedPosition);
    }

    public BrandSelectionAdapter(@NonNull OnBrandSelectListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemBrandSelectionBinding binding;

        public ViewHolder(@NonNull final ListItemBrandSelectionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                // Get the current checked position.
                setCheckedPosition(getAdapterPosition());

                // Notify the listener that a new position has been selected
                listener.onClick(checkedPosition);
            });
        }

        public void bind(@NonNull final Brand brand) {
            binding.setBrand(brand);

            // Update radio button based on the `checkedPosition`
            binding.rbSelect.setChecked(checkedPosition == getAdapterPosition());

            binding.executePendingBindings();
        }
    }
}