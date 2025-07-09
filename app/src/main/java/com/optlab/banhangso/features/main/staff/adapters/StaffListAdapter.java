package com.optlab.banhangso.features.main.staff.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemStaffBinding;
import com.optlab.banhangso.features.main.staff.models.StaffUiModel;
import java.util.function.Consumer;
import timber.log.Timber;

public class StaffListAdapter extends PagingDataAdapter<StaffUiModel, StaffListAdapter.ViewHolder> {

  private final Consumer<StaffUiModel> consumer;

  public StaffListAdapter(Consumer<StaffUiModel> consumer) {
    super(DIFF_CALLBACK);
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemStaffBinding binding =
        ListItemStaffBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    StaffUiModel staffUiModel = getItem(position);
    if (staffUiModel != null) {
      holder.bind(staffUiModel);
    } else {
      Timber.w("StaffUiModel at position %d is null", position);
      return;
    }

    holder.binding.getRoot().setOnClickListener(v -> consumer.accept(staffUiModel));
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ListItemStaffBinding binding;

    public ViewHolder(@NonNull ListItemStaffBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull StaffUiModel staffUiModel) {
      binding.setStaff(staffUiModel);
      binding.executePendingBindings();
    }
  }

  private static final DiffUtil.ItemCallback<StaffUiModel> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(
            @NonNull StaffUiModel oldItem, @NonNull StaffUiModel newItem) {
          return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull StaffUiModel oldItem, @NonNull StaffUiModel newItem) {
          return oldItem.equals(newItem);
        }
      };
}
