package com.optlab.banhangso.features.main.customer.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.paging.PagingDataAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.databinding.ListItemCustomerBinding;
import com.optlab.banhangso.features.main.customer.models.CustomerUiModel;
import java.util.function.Consumer;
import timber.log.Timber;

public class CustomerListAdapter
    extends PagingDataAdapter<CustomerUiModel, CustomerListAdapter.ViewHolder> {

  private final Consumer<String> idConsumer;
  private final Consumer<String> phoneConsumer;
  private final Consumer<String> emailConsumer;

  public CustomerListAdapter(
      Consumer<String> idConsumer, Consumer<String> phoneConsumer, Consumer<String> emailConsumer) {
    super(DIFF_CALLBACK);
    this.idConsumer = idConsumer;
    this.phoneConsumer = phoneConsumer;
    this.emailConsumer = emailConsumer;
  }

  @NonNull @Override
  public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    ListItemCustomerBinding binding =
        ListItemCustomerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
    return new ViewHolder(binding);
  }

  @Override
  public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    CustomerUiModel uiModel = getItem(position);
    if (uiModel == null) {
      Timber.e("CustomerUiModel at position %d is null", position);
      return;
    }

    holder.bind(uiModel); // Bind the customer data to the view

    // Set up the click listener for the item view`
    holder.binding.getRoot().setOnClickListener(v -> idConsumer.accept(uiModel.getId()));

    String phoneNumber = uiModel.getPhone();
    if (phoneNumber != null && !phoneNumber.isBlank()) {
      // Set up the click listener for the call button
      holder.binding.ibCall.setOnClickListener(v -> phoneConsumer.accept(phoneNumber));
    }

    String email = uiModel.getPhone();
    if (email != null && !email.isBlank()) {
      // Set up the click listener for the email button
      holder.binding.ibEmail.setOnClickListener(v -> emailConsumer.accept(email));
    }
  }

  public static class ViewHolder extends RecyclerView.ViewHolder {

    private final ListItemCustomerBinding binding;

    public ViewHolder(@NonNull ListItemCustomerBinding binding) {
      super(binding.getRoot());
      this.binding = binding;
    }

    public void bind(@NonNull CustomerUiModel customerUiModel) {
      binding.setCustomer(customerUiModel);
      binding.executePendingBindings();
    }
  }

  private static final DiffUtil.ItemCallback<CustomerUiModel> DIFF_CALLBACK =
      new DiffUtil.ItemCallback<>() {
        @Override
        public boolean areItemsTheSame(
            @NonNull CustomerUiModel oldItem, @NonNull CustomerUiModel newItem) {
          return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(
            @NonNull CustomerUiModel oldItem, @NonNull CustomerUiModel newItem) {
          return oldItem.equals(newItem);
        }
      };
}
