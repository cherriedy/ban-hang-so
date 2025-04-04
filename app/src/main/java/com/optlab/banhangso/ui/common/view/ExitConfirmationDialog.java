package com.optlab.banhangso.ui.common.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.optlab.banhangso.R;


public class ExitConfirmationDialog extends DialogFragment {
    /**
     * Request key for the exit confirmation dialog result.
     */
    public static final String REQUEST = "exit_confirmation";
    /**
     * Result key for the exit confirmation dialog.
     */
    public static final String CONFIRMED = "confirmed";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireActivity());
        builder.setTitle(R.string.alert_exit_without_saving)
                .setMessage(R.string.alert_confirm_again)
                .setPositiveButton(R.string.agree, (
                        dialog, which) -> { // User clicked "Agree"
                    Bundle result = new Bundle();
                    result.putBoolean(CONFIRMED, true);
                    getParentFragmentManager().setFragmentResult(REQUEST, result);
                })
                .setNegativeButton(R.string.refuse, (
                        dialog, which) -> { // User clicked "Refuse"
                    Bundle result = new Bundle();
                    result.putBoolean(CONFIRMED, false);
                    getParentFragmentManager().setFragmentResult(REQUEST, result);
                })
                .setCancelable(false);
        return builder.create();
    }
}