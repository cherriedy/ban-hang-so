package com.optlab.banhangso.ui.common.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class DeleteConfirmationDialog extends DialogFragment {
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String POSITIVE_BUTTON_TEXT = "positive_button_text";
    public static final String NEGATIVE_BUTTON_TEXT = "negative_button_text";
    public static final String REQUEST = "delete_confirmation";
    public static final String RESULT = "deleted";

    public static DeleteConfirmationDialog newInstance(@NonNull String title,
                                                       @NonNull String message,
                                                       @NonNull String negativeButtonText,
                                                       @NonNull String positiveButtonText) {
        Bundle args = new Bundle();
        args.putString(TITLE, title);
        args.putString(MESSAGE, message);
        args.putString(POSITIVE_BUTTON_TEXT, positiveButtonText);
        args.putString(NEGATIVE_BUTTON_TEXT, negativeButtonText);
        DeleteConfirmationDialog fragment = new DeleteConfirmationDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("Arguments cannot be null");
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity())
                .setTitle(args.getString(TITLE))
                .setMessage(args.getString(MESSAGE))
                .setPositiveButton(
                        args.getString(POSITIVE_BUTTON_TEXT),
                        (dialog, which) -> {
                            Bundle result = new Bundle();
                            result.putBoolean(RESULT, true);
                            getParentFragmentManager().setFragmentResult(REQUEST, result);
                        })
                .setNegativeButton(
                        args.getString(NEGATIVE_BUTTON_TEXT),
                        (dialog, which) -> {
                            Bundle result = new Bundle();
                            result.putBoolean(RESULT, false);
                            getParentFragmentManager().setFragmentResult(REQUEST, result);
                        })
                .setCancelable(false);
        return builder.create();
    }
}