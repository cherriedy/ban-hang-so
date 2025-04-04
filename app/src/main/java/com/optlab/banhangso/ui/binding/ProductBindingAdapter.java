package com.optlab.banhangso.ui.binding;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;

public class ProductBindingAdapter {
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
