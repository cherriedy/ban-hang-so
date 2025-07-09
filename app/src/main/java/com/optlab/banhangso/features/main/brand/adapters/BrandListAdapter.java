package com.optlab.banhangso.features.main.brand.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemBrandBinding;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import java.util.function.Consumer;
import timber.log.Timber;

public class BrandListAdapter extends PagingDataAdapter<BrandUiModel, BrandListAdapter.ViewHolder> {

  @NonNull private final Consumer<String> consumer;

  public BrandListAdapter(@NonNull Consumer<String> consumer) {
    super(DIFF_CALLBACK);
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemBrandBinding binding =
        ListItemBrandBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    BrandUiModel brand = getItem(position);
    if (brand == null) {
      Timber.e("Brand at position %d is null", position);
      return;
    }
    holder.bind(brand);
    holder.binding.getRoot().setOnClickListener(v -> consumer.accept(brand.getId()));
  }

  @Nullable public BrandUiModel getBrandAt(int position) {
    return getItem(position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ListItemBrandBinding binding;

    public ViewHolder(@NonNull ListItemBrandBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull BrandUiModel brand) {
      binding.setBrand(brand);
      binding.executePendingBindings();
    }
  }

  private static final DiffUtil.ItemCallback<BrandUiModel> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(
            @NonNull BrandUiModel oldItem, @NonNull BrandUiModel newItem) {
          return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull BrandUiModel oldItem, @NonNull BrandUiModel newItem) {
          return oldItem.equals(newItem);
        }
      };
}
