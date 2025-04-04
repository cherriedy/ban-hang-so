package com.optlab.banhangso.ui.binding;

import androidx.databinding.InverseMethod;
import com.optlab.banhangso.R;

public class BindingAdapterConverters {
  /**
   * Convert boolean status to button id for radio button group in layout.
   *
   * @param status boolean status of product
   * @return R.id.rb_in_stock if status is true, R.id.rb_out_stock if status is false
   */
  @InverseMethod("buttonIdToStatus")
  public static int statusToButtonId(boolean status) {
    return status ? R.id.rb_in_stock : R.id.rb_out_stock;
  }

  /**
   * Convert button id to boolean status for radio button group in layout.
   *
   * @param buttonId button id of radio button group (R.id.rb_in_stock or R.id.rb_out_stock)
   * @return true if button id is R.id.rb_in_stock, false if button id is R.id.rb_out_stock
   */
  public static boolean buttonIdToStatus(int buttonId) {
    return buttonId == R.id.rb_in_stock;
  }
}
