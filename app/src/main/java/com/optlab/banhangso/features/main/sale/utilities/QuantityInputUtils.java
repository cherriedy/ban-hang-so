package com.optlab.banhangso.features.main.sale.utilities;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.sale.listeners.CartItemListener;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;
import lombok.experimental.UtilityClass;
import timber.log.Timber;

@UtilityClass
public class QuantityInputUtils {

  public static void configureQuantityEditText(
      @NonNull EditText editText,
      @NonNull CartUiModel.Item item,
      @NonNull CartItemListener listener) {

    editText.addTextChangedListener(
        new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // No action needed before text changes
          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
            syncQuantityFromEditText(editText, item, listener);
          }

          @Override
          public void afterTextChanged(Editable s) {
            syncQuantityFromEditText(editText, item, listener);
          }
        });
  }

  private static void syncQuantityFromEditText(
      @NonNull EditText editText,
      @NonNull CartUiModel.Item item,
      @NonNull CartItemListener callback) {

    try {
      String text = editText.getText().toString().trim();
      Timber.d("Quantity from EditText: %s", text);
      int newQuantity = text.isEmpty() ? 0 : Integer.parseInt(text);

      // Update the item quantity (this applies validation)
      int oldQuantity = item.getQuantity();
      item.setQuantity(newQuantity);

      // Update EditText if quantity was adjusted by validation
      if (item.getQuantity() != newQuantity) {
        editText.setText(String.valueOf(item.getQuantity()));
      }

      // Only notify if quantity actually changed
      if (oldQuantity != item.getQuantity()) {
        callback.onQuantityChanged(item);
        Timber.d("Quantity updated via EditText: %s -> %d", item.getName(), item.getQuantity());
      }

    } catch (NumberFormatException e) {
      editText.setText(String.valueOf(item.getQuantity()));
      Timber.w("Invalid quantity input, reset to %d", item.getQuantity());
    }
  }
}
