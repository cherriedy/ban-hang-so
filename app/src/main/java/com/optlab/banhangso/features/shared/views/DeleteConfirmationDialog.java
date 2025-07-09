package com.optlab.banhangso.features.shared.views;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.optlab.banhangso.databinding.FragmentDeleteConfirmationDialogBinding;

public class DeleteConfirmationDialog extends DialogFragment {

  public static final String TITLE = "title";
  public static final String MESSAGE = "message";
  public static final String REQUEST = "delete_confirmation";
  public static final String DELETED = "deleted";

  @NonNull public static DeleteConfirmationDialog newInstance(
      @NonNull String title, @NonNull String message) {
    Bundle args = new Bundle();
    args.putString(TITLE, title);
    args.putString(MESSAGE, message);
    DeleteConfirmationDialog fragment = new DeleteConfirmationDialog();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    FragmentDeleteConfirmationDialogBinding binding =
        FragmentDeleteConfirmationDialogBinding.inflate(getLayoutInflater(), null, false);

    Bundle args = getArguments();
    if (args != null) {
      String title = args.getString(TITLE);
      if (title != null && !title.isBlank()) {
        binding.tvTitle.setText(title);
      }

      String message = args.getString(MESSAGE);
      if (message != null && !message.isBlank()) {
        binding.tvMessage.setText(message);
      }
    }

    binding.btnCancel.setOnClickListener(
        v -> {
          Bundle result = new Bundle();
          result.putBoolean(DELETED, false);
          getParentFragmentManager().setFragmentResult(REQUEST, result);
          dismiss();
        });

    binding.btnConfirm.setOnClickListener(
        v -> {
          Bundle result = new Bundle();
          result.putBoolean(DELETED, true);
          getParentFragmentManager().setFragmentResult(REQUEST, result);
          dismiss();
        });

    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext());
    builder.setView(binding.getRoot());
    builder.setCancelable(false);
    return builder.create();
  }
}
