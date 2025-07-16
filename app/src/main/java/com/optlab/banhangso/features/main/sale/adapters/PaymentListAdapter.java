package com.optlab.banhangso.features.main.sale.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemPaymentMethodBinding;
import com.optlab.banhangso.models.application.Payment;
import java.util.List;
import java.util.function.Consumer;

public class PaymentListAdapter extends RecyclerView.Adapter<PaymentListAdapter.ViewHolder> {

  @NonNull private final List<Payment> methods = Payment.getMethods();
  @NonNull private final Consumer<Payment.Method> consumer;

  private int selectedPosition = 0;

  public PaymentListAdapter(@NonNull Consumer<Payment.Method> consumer) {
    this.consumer = consumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemPaymentMethodBinding binding =
        ListItemPaymentMethodBinding.inflate(
            LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Payment payment = methods.get(position);
    holder.bind(payment);
  }

  @Override
  public int getItemCount() {
    return methods.size();
  }

  public void setSelectedPosition(int newPosition) {
    if (newPosition == RecyclerView.NO_POSITION || newPosition == selectedPosition) return;

    int currentPosition = selectedPosition; // Store the current selected position.
    selectedPosition = newPosition; // Update the selected position.
    if (currentPosition != RecyclerView.NO_POSITION) {
      // Notify the previous selected item to refresh its state.
      notifyItemChanged(currentPosition);
    }

    // Notify the new selected item to refresh its state.
    notifyItemChanged(selectedPosition);
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    private final ListItemPaymentMethodBinding binding;

    public ViewHolder(@NonNull ListItemPaymentMethodBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull Payment method) {
      binding.setMethod(method);

      boolean isSelected = getBindingAdapterPosition() == selectedPosition;
      itemView.setActivated(isSelected);
      binding.executePendingBindings();

      binding
          .getRoot()
          .setOnClickListener(
              v -> {
                setSelectedPosition(getBindingAdapterPosition());
                consumer.accept(Payment.getType(method));
              });
    }
  }
}
