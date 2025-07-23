package com.optlab.banhangso.features.shared.views.filters.interval;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.GridItemFilterBinding;
import com.optlab.banhangso.models.application.Interval;
import java.util.List;
import java.util.function.Consumer;

public class IntervalFilterListAdapter
    extends RecyclerView.Adapter<IntervalFilterListAdapter.ViewHolder> {

  @NonNull private final List<Interval> intervals = Interval.getIntervals();
  @NonNull private final Consumer<Interval> consumer;

  private int selectedPosition = RecyclerView.NO_POSITION;

  public IntervalFilterListAdapter(@NonNull Consumer<Interval> consumer) {
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    GridItemFilterBinding binding =
        GridItemFilterBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    holder.bind(intervals.get(position));
  }

  public void setSelectedPosition(@NonNull Interval interval) {
    int intervalPosition = intervals.indexOf(interval);
    // If the position is -1, it means the interval is not found in the list.
    // If the interval is not found, we do not change the selected position.
    if (intervalPosition == -1) return;
    setSelectedPosition(intervalPosition);
  }

  public void setSelectedPosition(int newPosition) {
    if (newPosition == selectedPosition) {
      selectedPosition = RecyclerView.NO_POSITION;
      notifyItemChanged(newPosition);
      return;
    }

    int currentPosition = selectedPosition;
    selectedPosition = newPosition;
    if (currentPosition != RecyclerView.NO_POSITION) {
      notifyItemChanged(currentPosition);
    }
    notifyItemChanged(selectedPosition);
  }

  @Override
  public int getItemCount() {
    return intervals.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private final GridItemFilterBinding binding;

    public ViewHolder(@NonNull GridItemFilterBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull Interval interval) {
      binding.setFilter(interval);

      int currentPosition = getBindingAdapterPosition();
      boolean checked = selectedPosition == currentPosition;
      binding.chipInterval.setChecked(checked);
      binding.chipInterval.setCheckedIconVisible(checked);

      binding.executePendingBindings();

      binding
          .getRoot()
          .setOnClickListener(__ -> onItemSelected(interval, currentPosition, checked));
    }

    private void onItemSelected(@NonNull Interval interval, int currentPosition, boolean checked) {
      setSelectedPosition(currentPosition); // Toggle the selection state.
      // Notify the consumer with the selected interval or null if unselected.
      consumer.accept(checked ? null : interval);
    }
  }
}
