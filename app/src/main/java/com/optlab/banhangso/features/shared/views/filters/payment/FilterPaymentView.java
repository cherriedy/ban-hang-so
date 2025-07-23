package com.optlab.banhangso.features.shared.views.filters.payment;

import static com.optlab.banhangso.features.shared.utilities.ViewUtils.setAnimatedVisibility;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FilterPricesBinding;
import com.optlab.banhangso.internal.utilities.PriceFormatter;
import com.optlab.banhangso.internal.utilities.itemspacing.GridSpacingStrategy;
import com.optlab.banhangso.internal.utilities.itemspacing.SpacingItemDecoration;
import com.optlab.banhangso.models.application.Payment;
import java.text.DecimalFormat;
import java.text.ParseException;
import timber.log.Timber;

public class FilterPaymentView extends LinearLayout {

  public interface OnFilterSelectedListener {
    void onPaymentMethodChanged(@Nullable Payment payment);

    void onPriceFromChanged(@Nullable Double price);

    void onPriceToChanged(@Nullable Double price);
  }

  private final Context context;
  private final FilterPricesBinding binding;
  private final DecimalFormat decimalFormat = PriceFormatter.getInstance();

  private OnFilterSelectedListener listener;
  private PaymentFilterListAdapter listAdapter;

  public FilterPaymentView(Context context, @Nullable AttributeSet attrs) {
    super(context, attrs);
    this.context = context;

    binding = FilterPricesBinding.inflate(LayoutInflater.from(context), this, true);
    listAdapter =
        new PaymentFilterListAdapter(
            method -> {
              if (listener != null) {
                listener.onPaymentMethodChanged(method);
              }
            });
    binding.rvPayments.setAdapter(listAdapter);

    binding.ibPriceDrop.setOnClickListener(v -> togglePriceSection());

    setUpPaymentItemSpacing(); // Set up the RecyclerView with spacing.
    binding.etPriceFrom.addTextChangedListener(new PriceTextWatcher(binding.etPriceFrom));
    binding.etPriceTo.addTextChangedListener(new PriceTextWatcher(binding.etPriceTo));
  }

  public void addOnFilterSelectedListener(
      @NonNull OnFilterSelectedListener onFilterSelectedListener) {
    listener = onFilterSelectedListener;
  }

  public void clearPaymentMethod() {
    if (listAdapter != null) {
      // Set the selected position to NO_POSITION to unselect any payment method.
      listAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
      // Notify the listener that no payment method is selected.
      listener.onPaymentMethodChanged(null);
    }
  }

  public void setSelectedPaymentMethod(@Nullable Payment payment) {
    if (listAdapter != null) {
      if (payment != null) {
        // Set the selected position to the position of the payment method.
        listAdapter.setSelectedPosition(payment);
      } else {
        // If payment is null, set the selected position to NO_POSITION to unselect any method.
        listAdapter.setSelectedPosition(RecyclerView.NO_POSITION);
      }
    }
  }

  public void setSelectedPriceFrom(@Nullable Double selectedPriceFrom) {
    if (selectedPriceFrom != null) {
      String formattedPrice = decimalFormat.format(selectedPriceFrom);
      binding.etPriceFrom.setText(formattedPrice);
    } else {
      binding.etPriceFrom.setText("");
    }
  }

  public void setSelectedPriceTo(@Nullable Double selectedPriceTo) {
    if (selectedPriceTo != null) {
      String formattedPrice = decimalFormat.format(selectedPriceTo);
      binding.etPriceTo.setText(formattedPrice);
    } else {
      binding.etPriceTo.setText("");
    }
  }

  private void togglePriceSection() {
    boolean visibility = binding.llPriceContent.getVisibility() == View.VISIBLE;
    setAnimatedVisibility(binding.llPriceContent, visibility);
    binding.ibPriceDrop.setImageResource(
        !visibility ? R.drawable.ic_drop_up : R.drawable.ic_drop_down);
  }

  private void setUpPaymentItemSpacing() {
    GridSpacingStrategy gridSpacingStrategy = new GridSpacingStrategy(context, 8);
    SpacingItemDecoration spacingItemDecoration = new SpacingItemDecoration(gridSpacingStrategy);
    binding.rvPayments.addItemDecoration(spacingItemDecoration);
  }

  private final class PriceTextWatcher implements TextWatcher {

    private final EditText editText;

    @NonNull private String oldPrice = "";

    private PriceTextWatcher(@NonNull EditText editText) {
      this.editText = editText;
    }

    @Override
    public void beforeTextChanged(@NonNull CharSequence s, int start, int count, int after) {
      oldPrice = s.toString(); // Store the price before change.
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override
    public void afterTextChanged(Editable newValue) {
      processPriceChange(newValue, editText);
    }

    private void processPriceChange(Editable newValue, @NonNull EditText view) {
      if (listener == null) {
        Timber.w("Listener is null, cannot process price change.");
        return;
      }

      if (newValue == null || newValue.toString().isEmpty()) {
        listener.onPriceFromChanged(null);
        return;
      }

      Double newPrice = null; // Default value if parsing fails.
      try {
        // Parse the new value using the DecimalFormat instance.
        Number parsed = decimalFormat.parse(newValue.toString());
        // If parsing was successful, convert it to double.
        if (parsed != null) newPrice = parsed.doubleValue();
      } catch (ParseException | NullPointerException e) {
        Timber.e(e, "There was an error parsing the price: %s", e.getMessage());
      }

      String newPriceValue = decimalFormat.format(newPrice);
      if (!(oldPrice.equals(newPriceValue))) {
        view.removeTextChangedListener(this);
        view.setText(newPriceValue);
        view.setSelection(newPriceValue.length());
        view.addTextChangedListener(this);

        if (listener != null) {
          int id = view.getId();
          if (id == R.id.et_price_from) listener.onPriceFromChanged(newPrice);
          else if (id == R.id.et_price_to) listener.onPriceToChanged(newPrice);
        }
      }
    }
  }
}
