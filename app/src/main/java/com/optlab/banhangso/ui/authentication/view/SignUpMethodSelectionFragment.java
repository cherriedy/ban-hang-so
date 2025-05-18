package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentSignUpMethodSelectionBinding;

public class SignUpMethodSelectionFragment extends BottomSheetDialogFragment {
    private FragmentSignUpMethodSelectionBinding binding;
    private NavController navController;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentSignUpMethodSelectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = NavHostFragment.findNavController(requireParentFragment());

        binding.llSignInWithEmail.setOnClickListener(
                v -> navController.navigate(R.id.signUpWithEmailFragment));

        binding.llSignInWithPhone.setOnClickListener(
                v -> navController.navigate(R.id.signUpWithPhoneNumberFragment));

        binding.tvSignIn.setOnClickListener(v -> navController.popBackStack());
    }
}
