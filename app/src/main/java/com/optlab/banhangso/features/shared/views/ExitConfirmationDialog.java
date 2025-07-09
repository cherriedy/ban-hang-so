package com.optlab.banhangso.features.shared.views;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.optlab.banhangso.databinding.FragmentExitConfirmationDialogBinding;

public class ExitConfirmationDialog extends DialogFragment {
  /** Request key for the exit confirmation dialog result. */
  public static final String REQUEST = "exit_confirmation";

  /** Result key for the exit confirmation dialog. */
  public static final String CONFIRMED = "confirmed";

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    FragmentExitConfirmationDialogBinding binding =
        FragmentExitConfirmationDialogBinding.inflate(getLayoutInflater(), null, false);

    binding.btnCancel.setOnClickListener(
        v -> {
          Bundle result = new Bundle();
          result.putBoolean(CONFIRMED, false);
          getParentFragmentManager().setFragmentResult(REQUEST, result);
          dismiss();
        });

    binding.btnConfirm.setOnClickListener(
        v -> {
          Bundle result = new Bundle();
          result.putBoolean(CONFIRMED, true);
          getParentFragmentManager().setFragmentResult(REQUEST, result);
          dismiss();
        });

    // Create and return the dialog with custom view
    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
    builder.setView(binding.getRoot());
    builder.setCancelable(false);

    return builder.create();
  }
}
