package com.optlab.banhangso.util.validator;

import android.content.Context;
import android.text.TextUtils;

import com.optlab.banhangso.R;

import javax.inject.Inject;

public final class ProductValidator {
    private final Context context;

    @Inject
    public ProductValidator(Context context) {
        this.context = context;
    }

    public String validateName(String name) {
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

    public String validateSellingPrice(Double sellingPrice) {
        if (sellingPrice == 0) {
            return context.getString(R.string.alert_product_price_non_null);
        } else if (sellingPrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else {
            return "";
        }
    }

    public String validatePurchasePrice(Double purchasePrice, Double sellingPrice) {
        if (purchasePrice > sellingPrice) {
            return context.getString(R.string.alert_product_purchase_price_invalid);
        } else if (purchasePrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else {
            return "";
        }
    }

    public String validateDiscountPrice(Double discountPrice, Double sellingPrice) {
        if (discountPrice == null) {
            return context.getString(R.string.alert_product_price_non_null);
        } else if (discountPrice < 0) {
            return context.getString(R.string.alert_product_price_invalid);
        } else if (discountPrice > sellingPrice) {
            return context.getString(R.string.alert_product_discount_price_invalid);
        } else {
            return "";
        }
    }

    public String validateDescription(String description) {
        if (description.length() > 120) {
            return context.getString(R.string.alert_product_description_max_chars);
        } else {
            return "";
        }
    }

    public String validateNote(String note) {
        if (note.length() > 50) {
            return context.getString(R.string.alert_product_note_max_chars);
        } else {
            return "";
        }
    }
}
