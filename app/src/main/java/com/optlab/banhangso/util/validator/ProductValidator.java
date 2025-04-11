package com.optlab.banhangso.util.validator;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.Brand;
import com.optlab.banhangso.data.model.Category;

import javax.inject.Inject;

public final class ProductValidator {
    private final Context context;

    @Inject
    public ProductValidator(Context context) {
        this.context = context;
    }

    public String validateName(@NonNull String name) {
        if (TextUtils.isEmpty(name)) {
            return context.getString(R.string.alert_product_name_non_null);
        } else if (name.length() < 10) {
            return context.getString(R.string.alert_product_name_min_chars);
        } else if (name.length() > 100) {
            return context.getString(R.string.alter_product_name_max_chars);
        } else {
            return "";
        }
    }

    public String validateSellingPrice(@NonNull Double sellingPrice) {
        if (sellingPrice == 0) {
            return context.getString(R.string.alert_product_price_non_null);
        } else if (sellingPrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else {
            return "";
        }
    }

    public String validatePurchasePrice(
            @NonNull Double purchasePrice, @NonNull Double sellingPrice) {
        if (purchasePrice > sellingPrice) {
            return context.getString(R.string.alert_product_purchase_price_invalid);
        } else if (purchasePrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else {
            return "";
        }
    }

    public String validateDiscountPrice(
            @NonNull Double discountPrice, @NonNull Double sellingPrice) {
        if (discountPrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else if (discountPrice > sellingPrice) {
            return context.getString(R.string.alert_product_discount_price_invalid);
        } else {
            return "";
        }
    }

    public String validateDescription(@Nullable String description) {
        if (description != null && description.length() > 120) {
            return context.getString(R.string.alert_product_description_max_chars);
        } else {
            return "";
        }
    }

    public String validateNote(@Nullable String note) {
        if (note != null && note.length() > 50) {
            return context.getString(R.string.alert_product_note_max_chars);
        } else {
            return "";
        }
    }

    public String validateBrand(Brand brand) {
        if (brand == null || brand.isEmpty()) {
            return context.getString(R.string.alert_product_brand_non_null);
        } else {
            return "";
        }
    }

    public String validateCategory(Category category) {
        if (category == null || category.isEmpty()) {
            return context.getString(R.string.alert_product_category_non_null);
        } else {
            return "";
        }
    }
}
