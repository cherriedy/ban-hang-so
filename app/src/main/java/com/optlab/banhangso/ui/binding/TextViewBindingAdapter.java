package com.optlab.banhangso.ui.binding;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.BindingAdapter;

import com.optlab.banhangso.R;
import com.google.android.material.textfield.TextInputLayout;

public class TextViewBindingAdapter {

    @BindingAdapter("syncFocusColor")
    public static void setSyncFocusColor(@NonNull final TextView view, @NonNull final EditText editText) {

        Runnable updateColor = () -> {
            TextInputLayout layout = findTextInputLayout(editText);
            // If the TextInputLayout has an error set, use the error focusColor.
            // Otherwise, use primary when focused; default when not.
            int focusColor;
            if (layout != null && layout.getError() != null) {
                focusColor = ContextCompat.getColor(view.getContext(), R.color.color_warning);
            } else {
                if (editText.hasFocus()) {
                    focusColor = ContextCompat.getColor(view.getContext(), R.color.color_primary);
                } else {
                    focusColor = ContextCompat.getColor(view.getContext(), R.color.color_text_title);
                }
            }
            view.setTextColor(focusColor);
        };

        // Set up the focus listener.
        editText.setOnFocusChangeListener((v, hasFocus) -> updateColor.run());

        // Also update the color when the text changes.
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateColor.run();
            }
        });
    }

    /**
     * Sets the error message for a TextInputLayout.
     *
     * @param view  The TextInputLayout to set the error message for.
     * @param error The error message to set. If null or empty, the error will be cleared.
     */
    @BindingAdapter("error")
    public static void setError(@NonNull TextInputLayout view, String error) {
        if (error == null || error.isEmpty()) {
            view.setError(null);
            view.setErrorEnabled(false);
        } else {
            view.setError(error);
            view.setErrorEnabled(true);
        }
    }

    // Helper method to walk up the view hierarchy and return the first TextInputLayout
    private static TextInputLayout findTextInputLayout(EditText editText) {
        ViewParent parent = editText.getParent();
        while (parent instanceof View) {
            if (parent instanceof TextInputLayout) {
                return (TextInputLayout) parent;
            }
            parent = parent.getParent();
        }
        return null;
    }
}
