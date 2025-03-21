package com.optlab.banhangso.ui.binding;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import com.optlab.banhangso.R;
import com.optlab.banhangso.util.PriceFormatUtils;

import java.text.DecimalFormat;
import java.text.ParsePosition;

public class PriceBindingAdapter {

    private static final DecimalFormat DECIMAL_FORMAT = PriceFormatUtils.getInstance();

    @BindingAdapter("price")
    public static void setPrice(@NonNull EditText view, double value) {
        String newText = DECIMAL_FORMAT.format(value);
        String oldText = view.getText().toString();
        if (!newText.equals(oldText)) {
            view.setText(newText);
            view.setSelection(newText.length());
        }
    }

    @InverseBindingAdapter(attribute = "price", event = "priceAttrChanged")
    public static double getPrice(@NonNull EditText view) {
        String text = view.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return 0.0;
        }
        try {
            ParsePosition parsePosition = new ParsePosition(0);
            Number number = DECIMAL_FORMAT.parse(text, parsePosition);
            if (number == null || parsePosition.getIndex() != text.length()) {
                return 0.0;
            }
            return number.doubleValue();
        } catch (Exception e) {
            return 0.0;
        }
    }

    @BindingAdapter("priceAttrChanged")
    public static void setPriceListeners(@NonNull EditText view, final InverseBindingListener attrChange) {
        final TextWatcher newWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (attrChange != null) {
                    attrChange.onChange();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        // Remove previously attached watcher using a view tag
        TextWatcher oldWatcher = (TextWatcher) view.getTag(R.id.decimal_value_watcher);
        if (oldWatcher != null) {
            view.removeTextChangedListener(oldWatcher);
        }
        view.addTextChangedListener(newWatcher);
        view.setTag(R.id.decimal_value_watcher, newWatcher);
    }
}
