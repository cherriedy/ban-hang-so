package com.optlab.banhangso.ui.binding;

import android.graphics.Paint;
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
    /**
     * Sets the text color of a TextView based on the focus state of an EditText.
     *
     * @param view The TextView to set the color for.
     * @param editText The EditText whose focus state is used to determine the color.
     */
    @BindingAdapter("syncFocusColor")
    public static void setSyncFocusColor(
            @NonNull final TextView view, @NonNull final EditText editText) {
        // Runnable to update the color of the TextView based on the EditText's focus state.
        Runnable updateColor =
                () -> {
                    // Find the parent TextInputLayout of the EditText.
                    TextInputLayout layout = findTextInputLayout(editText);

                    int focusColor;
                    if (layout != null && layout.getError() != null) {
                        // If the TextInputLayout has an error, set the color to warning.
                        focusColor =
                                ContextCompat.getColor(view.getContext(), R.color.color_warning);
                    } else {
                        if (editText.hasFocus()) {
                            // If the EditText has focus, set the color to primary.
                            focusColor =
                                    ContextCompat.getColor(
                                            view.getContext(), R.color.color_primary);
                        } else {
                            // If the EditText does not have focus, set the color to title.
                            focusColor =
                                    ContextCompat.getColor(
                                            view.getContext(), R.color.color_text_title);
                        }
                    }
                    view.setTextColor(focusColor);
                };

        // Update the color when the EditText gains or loses focus.
        editText.setOnFocusChangeListener((v, hasFocus) -> updateColor.run());

        // Update the color when the TextInputLayout has an error.
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void afterTextChanged(Editable s) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        updateColor.run();
                    }
                });
    }

    /**
     * Sets the error message for a TextInputLayout.
     *
     * @param view The TextInputLayout to set the error message for.
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

    /**
     * Finds the TextInputLayout that contains the given EditText.
     *
     * @param editText The EditText whose parent TextInputLayout is to be found.
     * @return The TextInputLayout that contains the EditText, or null if not found.
     */
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

    /**
     * Sets the strike-through effect on a TextView based on the boolean value.
     *
     * @param view The TextView to set the strike-through effect on.
     * @param strikeThrough True to apply the strike-through effect, false to remove it.
     */
    @BindingAdapter("strikeThrough")
    public static void setStrikeThrough(TextView view, boolean strikeThrough) {
        if (strikeThrough) {
            view.setPaintFlags(view.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            view.setPaintFlags(view.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }
    }
}
