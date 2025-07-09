package com.optlab.banhangso.features.main.product.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.BR;
import com.optlab.banhangso.R;
import com.optlab.banhangso.models.domain.Product;
import java.util.Objects;
import java.util.function.Consumer;

public class ProductListAdapter extends PagingDataAdapter<Product, ProductListAdapter.ViewHolder> {

  private final Consumer<String> consumer;
  private int itemLayoutRes = R.layout.list_item_product;

  public ProductListAdapter(@NonNull final Consumer<String> consumer) {
    super(new DiffCallback());
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ViewDataBinding binding =
        DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), viewType, parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Product product = getItem(position);
    if (product != null) {
      holder.bind(product);
      holder.binding.getRoot().setOnClickListener(v -> consumer.accept(product.getId()));
    }
  }

  @Override
  public int getItemViewType(int position) {
    return itemLayoutRes;
  }

  /** Setter to update the layout resource dynamically. */
  @SuppressLint("NotifyDataSetChanged")
  public void setItemLayoutRes(int itemLayoutRes) {
    this.itemLayoutRes = itemLayoutRes;
    notifyDataSetChanged();
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ViewDataBinding binding;

    public ViewHolder(@NonNull ViewDataBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(Product product) {
      binding.setVariable(BR.product, product);
      binding.executePendingBindings();
    }
  }

  private static class DiffCallback extends DiffUtil.ItemCallback<Product> {
    @Override
    public boolean areItemsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
      return Objects.equals(oldItem.getId(), newItem.getId());
    }

    @Override
    public boolean areContentsTheSame(@NonNull Product oldItem, @NonNull Product newItem) {
      return oldItem.equals(newItem);
    }
  }
}
