package com.optlab.banhangso.features.main.product.binding;

import android.text.TextUtils;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseMethod;
import com.optlab.banhangso.R;
import com.optlab.banhangso.features.main.brand.models.BrandUiModel;
import com.optlab.banhangso.features.main.category.models.CategoryUiModel;

public class ProductBindingAdapter {
  /**
   * Set brand name to TextView.
   *
   * @param view TextView to set brand name
   * @param brand Brand object
   */
  @BindingAdapter("brand")
  public static void setBrand(@NonNull TextView view, BrandUiModel brand) {
    if (brand != null && !TextUtils.isEmpty(brand.getName())) {
      view.setText(brand.getName());
    } else if (brand == null) {
      view.setText("");
    }
  }

  /**
   * Set category name to TextView.
   *
   * @param view TextView to set category name
   * @param category Category object
   */
  @BindingAdapter("category")
  public static void setCategory(@NonNull TextView view, CategoryUiModel category) {
    if (category != null && !TextUtils.isEmpty(category.getName())) {
      view.setText(category.getName());
    } else if (category == null) {
      view.setText("");
    }
  }

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
