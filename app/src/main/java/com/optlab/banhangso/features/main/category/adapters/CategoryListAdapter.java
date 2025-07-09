package com.optlab.banhangso.features.main.category.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemCategoryBinding;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;
import java.util.function.Consumer;
import timber.log.Timber;

public class CategoryListAdapter
    extends PagingDataAdapter<CategoryUiModel, CategoryListAdapter.ViewHolder> {

  @NonNull private final Consumer<String> consumer;

  public CategoryListAdapter(@NonNull Consumer<String> consumer) {
    super(DIFF_CALLBACK);
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    ListItemCategoryBinding binding = ListItemCategoryBinding.inflate(inflater, parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    CategoryUiModel uiModel = getItem(position);
    if (uiModel == null) {
      Timber.e("CategoryUiModel at position %d is null", position);
      return;
    }
    holder.bind(uiModel);
    holder.binding.getRoot().setOnClickListener(v -> consumer.accept(uiModel.getId()));
  }

  @Nullable public CategoryUiModel getCategoryAt(int position) {
    return getItem(position);
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemCategoryBinding binding;

    public ViewHolder(@NonNull ListItemCategoryBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull CategoryUiModel category) {
      binding.setCategory(category);
      binding.executePendingBindings();
    }
  }

  private static final DiffUtil.ItemCallback<CategoryUiModel> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(
            @NonNull CategoryUiModel oldItem, @NonNull CategoryUiModel newItem) {
          return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull CategoryUiModel oldItem, @NonNull CategoryUiModel newItem) {
          return oldItem.equals(newItem);
        }
      };
}
