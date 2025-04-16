package com.optlab.banhangso.util.validator;

import android.content.Context;
import android.text.TextUtils;

import com.optlab.banhangso.R;

public class BrandValidator {
    private final Context context;

    public BrandValidator(Context context) {
        this.context = context;
    }

    public String validateName(String name) {
        if (TextUtils.isEmpty(name)) {
            return context.getString(R.string.alter_brand_name_non_null);
        } else if (name.length() < 3) {
            return context.getString(R.string.alter_brand_name_min_chars);
        } else {
            return "";
        }
    }
}
