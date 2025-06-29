package com.optlab.banhangso.internal.validators;

import android.content.Context;
import android.text.TextUtils;
import com.optlab.banhangso.R;

public final class CategoryValidator {
  private final Context context;

  public CategoryValidator(Context context) {
    this.context = context;
  }

  public String validateName(String name) {
    if (TextUtils.isEmpty(name)) {
      return context.getString(R.string.alert_category_name_not_null);
    } else if (name.length() < 3) {
      return context.getString(R.string.alert_category_min_chars);
    } else if (name.length() > 50) {
      return context.getString(R.string.alert_category_max_chars);
    } else {
      return "";
    }
  }
}
