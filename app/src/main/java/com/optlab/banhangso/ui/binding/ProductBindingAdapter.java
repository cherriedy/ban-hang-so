package com.optlab.banhangso.ui.binding;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;

public class ProductBindingAdapter {
    /**
     * Set brand name to TextView.
     *
     * @param view  TextView to set brand name
     * @param brand Brand object
     */
    @BindingAdapter("brand")
    public static void setBrand(@NonNull TextView view, Brand brand) {
        if (brand != null && !TextUtils.isEmpty(brand.getName())) {
            view.setText(brand.getName());
        } else if (brand == null) {
            view.setText("");
        }
    }

    /**
     * Set category name to TextView.
     *
     * @param view     TextView to set category name
     * @param category Category object
     */
    @BindingAdapter("category")
    public static void setCategory(@NonNull TextView view, Category category) {
        if (category != null && !TextUtils.isEmpty(category.getName())) {
            view.setText(category.getName());
        } else if (category == null) {
            view.setText("");
        }
    }
}
