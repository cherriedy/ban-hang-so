package com.optlab.banhangso.ui.binding;

import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;
import com.optlab.banhangso.data.model.Product;

public class ProductBindingAdapter {
    @BindingAdapter("status")
    public static void setStatus(@NonNull RadioGroup view, @NonNull Product.ProductStatus status) {
        if (status == Product.ProductStatus.IN_STOCK) {
            view.check(R.id.rb_in_stock);
        } else {
            view.check(R.id.rb_out_stock);
        }
    }

    @InverseBindingAdapter(attribute = "status", event = "statusAttrChanged")
    public static Product.ProductStatus getStatus(@NonNull RadioGroup view) {
        if (view.getCheckedRadioButtonId() == R.id.rb_in_stock) {
            return Product.ProductStatus.IN_STOCK;
        }
        return Product.ProductStatus.OUT_STOCK;
    }

    @BindingAdapter("statusAttrChanged")
    public static void setStatusListeners(@NonNull RadioGroup view, final InverseBindingListener listener) {
        view.setOnCheckedChangeListener((group, checkedId) -> listener.onChange());
    }

    @BindingAdapter("brand")
    public static void setBrand(@NonNull TextView view, Brand brand) {
        if (brand != null) {
            view.setText(brand.getName());
        }
    }

    @BindingAdapter("category")
    public static void setCategory(@NonNull TextView view, Category category) {
        if (category != null) {
            view.setText(category.getName());
        }
    }
}
