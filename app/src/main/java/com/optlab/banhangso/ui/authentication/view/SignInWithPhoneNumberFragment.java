package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.optlab.banhangso.NavGraphSignInDirections;
import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.databinding.FragmentSignInWithPhoneNumberBinding;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;
import com.optlab.banhangso.ui.authentication.common.FirebaseAuthProvider;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.ui.authentication.viewmodel.SignInWithPhoneNumberViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import javax.inject.Inject;

@AndroidEntryPoint
public class SignInWithPhoneNumberFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected FirebaseAuthProvider firebaseAuthProvider;

    private FragmentSignInWithPhoneNumberBinding binding;
    private SignInWithPhoneNumberViewModel viewModel;
    private Observable.OnPropertyChangedCallback changedCallback;
    private AnimationLoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth.setLanguageCode("vi");
        firebaseAuth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        loadingDialog = new AnimationLoadingDialog();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SignInWithPhoneNumberViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInWithPhoneNumberBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void observeViewModel() {
        viewModel.getAuthData().observe(getViewLifecycleOwner(), this::onPropertyChange);
    }

    private void onPropertyChange(AuthData authData) {
        if (changedCallback == null) {
            changedCallback =
                    new Observable.OnPropertyChangedCallback() {
                        @Override
                        public void onPropertyChanged(Observable sender, int propertyId) {
                            handlePropertyChange((AuthData) sender, propertyId);
                        }
                    };
            authData.addOnPropertyChangedCallback(changedCallback);
        }
    }

    private void handlePropertyChange(AuthData authData, int propertyId) {
        switch (propertyId) {
            case BR.phone -> viewModel.validatePhoneNumber(authData.getPhoneNumber());
            case BR.password -> viewModel.validatePassword(authData.getPassword());
            default -> throw new IllegalStateException("Unexpected value: " + propertyId);
        }
    }

    /**
     * @noinspection unused
     */
    public void onLoginButtonClick(@NonNull View view) {
        AuthData authData = viewModel.getAuthData().getValue();
        if (authData != null) {
            // Validate the phone number and password before proceeding
            viewModel.validatePhoneNumber(viewModel.getAuthData().getValue().getPhoneNumber());
            viewModel.validatePassword(viewModel.getAuthData().getValue().getPassword());

            // If there are no validation errors, proceed with the login process
            AuthValidationState state = viewModel.getValidationState().getValue();
            if (state != null && state.isHasNoError()) {
                showLoadingProgress(); // Show animation loading dialog

                String phoneNumber = authData.getPhoneNumber();
                PhoneAuthProvider.verifyPhoneNumber(
                        firebaseAuthProvider.getPhoneAuthOption(
                                phoneNumber, getPhoneAuthCallbacks(phoneNumber)));
            }
        }
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getPhoneAuthCallbacks(
            @NonNull String phoneNumber) {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loadingDialog.dismiss();
                Toast.makeText(requireContext(), "Verify completed", Toast.LENGTH_SHORT).show();
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingDialog.dismiss();
                Toast.makeText(requireContext(), "Verify failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(
                    @NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken token) {
                loadingDialog.dismiss();
                navigateToVerificationFragment(phoneNumber, verificationId);
            }
        };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential) {
        firebaseAuth
                .signInWithCredential(phoneAuthCredential)
                .addOnSuccessListener(
                        authResult -> {
                            FirebaseUser user = authResult.getUser();
                        })
                .addOnFailureListener(
                        e -> {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                                requireContext(),
                                                "Invalid credentials",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            } else {
                                Toast.makeText(
                                                requireContext(),
                                                "Sign in failed",
                                                Toast.LENGTH_SHORT)
                                        .show();
                            }
                        });
    }

    private void showLoadingProgress() {
        loadingDialog.show(
                getParentFragmentManager(),
                SignInWithPhoneNumberFragment.this.getClass().getSimpleName());
    }

    private void navigateToVerificationFragment(String phoneNumber, String verificationId) {
        NavDirections action =
                NavGraphSignInDirections.actionLoginWithPhoneToVerification(
                        phoneNumber, verificationId);
        NavHostFragment.findNavController(this).navigate(action);
    }
}
