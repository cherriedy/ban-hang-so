package com.optlab.banhangso.ui.authentication.view;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.fragment.NavHostFragment;

import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSignUpMethodSelectionBinding;

public class SignUpMethodSelectionFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        FragmentSignUpMethodSelectionBinding binding =
                FragmentSignUpMethodSelectionBinding.inflate(getLayoutInflater(), null, false);
        dialog.setContentView(binding.getRoot());

        binding.mbSignupEmail.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(requireParentFragment())
                            .navigate(R.id.nav_graph_sign_up_with_email);
                    dialog.dismiss();
                });

        binding.mbSignupPhone.setOnClickListener(
                v -> {
                    NavHostFragment.findNavController(requireParentFragment())
                            .navigate(R.id.nav_graph_sign_up_with_phone);
                    dialog.dismiss();
                });

        return dialog;
    }
}
