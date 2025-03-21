package com.optlab.banhangso.ui.binding;

import androidx.annotation.NonNull;
import androidx.databinding.InverseMethod;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Product;

public class BindingAdapterConverters {
    @InverseMethod("buttonIdToStatus")
    public static int statusToButtonId(@NonNull Product.ProductStatus status) {
        if (status == Product.ProductStatus.IN_STOCK) {
            return R.id.rb_in_stock;
        }
        return R.id.rb_out_stock;
    }

    public static Product.ProductStatus buttonIdToStatus(int buttonId) {
        if (buttonId == R.id.rb_in_stock) {
            return Product.ProductStatus.IN_STOCK;
        }
        return Product.ProductStatus.OUT_STOCK;
    }
}
