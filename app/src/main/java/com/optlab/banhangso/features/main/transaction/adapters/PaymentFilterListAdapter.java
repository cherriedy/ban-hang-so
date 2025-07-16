package com.optlab.banhangso.features.main.transaction.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.GridItemFilterBinding;
import com.optlab.banhangso.models.application.Payment;
import java.util.List;
import java.util.function.Consumer;

public class PaymentFilterListAdapter
    extends RecyclerView.Adapter<PaymentFilterListAdapter.ViewHolder> {

  @NonNull private final List<Payment> payments = Payment.getMethods();
  @NonNull private final Consumer<String> consumer;

  private int selectedPosition = RecyclerView.NO_POSITION;

  public PaymentFilterListAdapter(@NonNull Consumer<String> consumer) {
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
    holder.bind(payments.get(position));
  }

  private void setSelectedPosition(int newPosition) {
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
    return payments.size();
  }

  public class ViewHolder extends RecyclerView.ViewHolder {

    private final GridItemFilterBinding binding;

    public ViewHolder(@NonNull GridItemFilterBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull Payment payment) {
      binding.setFilter(payment);

      int currentPosition = getLayoutPosition();
      boolean checked = selectedPosition == currentPosition;
      binding.chipInterval.setChecked(checked);
      binding.chipInterval.setCheckedIconVisible(checked);

      binding.executePendingBindings();

      binding
          .getRoot()
          .setOnClickListener(
              v -> {
                setSelectedPosition(currentPosition);
                consumer.accept(payment.getValue());
              });
    }
  }
}
