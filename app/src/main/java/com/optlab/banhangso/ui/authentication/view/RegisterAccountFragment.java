package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.optlab.banhangso.NavGraphSignUpWithPhoneDirections;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.databinding.FragmentRegisterAccountBinding;
import com.optlab.banhangso.ui.authentication.common.FirebaseAuthProvider;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpWithPhoneNumberViewModel;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class RegisterAccountFragment extends Fragment {
    private FragmentRegisterAccountBinding binding;
    private SignUpWithPhoneNumberViewModel viewModel;
    private AnimationLoadingDialog loadingDialog;

    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected FirebaseAuthProvider firebaseAuthProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        loadingDialog = new AnimationLoadingDialog();
        initViewModel();
    }

    private void initViewModel() {
        NavBackStackEntry navBackStackEntry =
                NavHostFragment.findNavController(this)
                        .getBackStackEntry(R.id.nav_graph_sign_up_with_phone);
        viewModel =
                new ViewModelProvider(navBackStackEntry).get(SignUpWithPhoneNumberViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterAccountBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void onCompleteButtonClick(@NonNull View view) {
        AuthData authData = viewModel.getAuthData().getValue();
        if (authData != null) {
            loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
            String phoneNumber = authData.getPhoneNumber();
            PhoneAuthProvider.verifyPhoneNumber(
                    firebaseAuthProvider.getPhoneAuthOption(
                            phoneNumber, getPhoneAuthCallbacks(phoneNumber)));
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getPhoneAuthCallbacks(
            @NonNull String phoneNumber) {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loadingDialog.dismiss();
                Toast.makeText(requireContext(), "Verify successful", Toast.LENGTH_SHORT).show();
                signUpWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingDialog.dismiss();
                Toast.makeText(requireContext(), "Verify failed", Toast.LENGTH_SHORT).show();
                Timber.e("onVerificationFailed: %s", e.getMessage());
            }

            @Override
            public void onCodeSent(
                    @NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                loadingDialog.dismiss();
                navigateToVerificationFragment(phoneNumber, verificationId);
            }
        };
    }

    private void navigateToVerificationFragment(String phoneNumber, String verificationId) {
        NavDirections action =
                NavGraphSignUpWithPhoneDirections.actionRegisterAccountToOtpVerification(
                        phoneNumber, verificationId);
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void signUpWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth
                .signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(authResult -> {})
                .addOnFailureListener(e -> {});
    }
}
