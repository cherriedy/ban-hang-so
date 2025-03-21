package com.optlab.banhangso.ui.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.databinding.library.baseAdapters.BR;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.optlab.banhangso.R;
import com.optlab.banhangso.ui.listener.OnProductClickListener;
import com.optlab.banhangso.data.model.Product;

public class ProductListAdapter extends ListAdapter<Product, ProductListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Product> CALL_BACK = new DiffUtil.ItemCallback<Product>() {
        @Override
        public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
            return oldItem.getName().equals(newItem.getName())
//                    && oldItem.getBrand().getName().equals(newItem.getBrand().getName())
//                    && oldItem.getCategory().getName().equals(newItem.getCategory().getName())
                    && oldItem.getSellingPrice() == newItem.getSellingPrice();
        }
    };

    private final OnProductClickListener listener;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private int itemLayoutRes = R.layout.list_item_horizontal_product;

    public ProductListAdapter(@NonNull final OnProductClickListener listener) {
        super(CALL_BACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding binding = DataBindingUtil.inflate(
                inflater, viewType, parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position) {
        return itemLayoutRes;
    }

    /**
     * Setter to update the layout resource dynamically.
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setItemLayoutRes(int itemLayoutRes) {
        this.itemLayoutRes = itemLayoutRes;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewDataBinding binding;

        public ViewHolder(@NonNull ViewDataBinding binding) {
            super(binding.getRoot());

            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                // Get the selected position in RecyclerView.
                selectedPosition = getBindingAdapterPosition();

                // If the position is valid, triggering the listener to send the id of product.
                if (selectedPosition != RecyclerView.NO_POSITION) {
                    listener.onClick(getItem(selectedPosition).getId());
                }
            });
        }

        public void bind(Product product) {
            binding.setVariable(BR.product, product);
            binding.executePendingBindings();
        }
    }
}