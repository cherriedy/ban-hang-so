package com.optlab.banhangso.features.main.sale.listeners;

import androidx.annotation.NonNull;
import com.optlab.banhangso.features.main.sale.models.CartUiModel;

/**
 * Unified callback interface for handling cart item quantity changes and removal. This interface
 * provides a clean API for both product list and cart adapters.
 */
public interface CartItemListener {

  /**
   * Called when the quantity of an item changes via any method (buttons, EditText, etc.)
   *
   * @param item The cart item that had its quantity modified
   */
  void onQuantityChanged(@NonNull CartUiModel.Item item);

  /**
   * Called when an item should be removed from the cart
   *
   * @param item The cart item to remove
   */
  void onItemRemoved(@NonNull CartUiModel.Item item);
}
