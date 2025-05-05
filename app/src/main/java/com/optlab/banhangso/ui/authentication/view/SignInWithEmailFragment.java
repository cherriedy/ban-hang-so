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
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.databinding.FragmentSignInWithEmailBinding;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;
import com.optlab.banhangso.ui.authentication.common.FirebaseAuthProvider;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.ui.authentication.viewmodel.SignInWithEmailViewModel;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class SignInWithEmailFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected FirebaseAuthProvider firebaseAuthProvider;

    private FragmentSignInWithEmailBinding binding;
    private SignInWithEmailViewModel viewModel;
    private Observable.OnPropertyChangedCallback changedCallback;
    private AnimationLoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth.setLanguageCode("vi");
        // firebaseAuth.getFirebaseAuthSettings().forceRecaptchaFlowForTesting(true);
        // firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        loadingDialog = new AnimationLoadingDialog();
        initViewModel();
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(SignInWithEmailViewModel.class);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignInWithEmailBinding.inflate(inflater, container, false);
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
            case BR.email -> viewModel.validateEmail(authData.getEmail());
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
            viewModel.validateEmail(viewModel.getAuthData().getValue().getEmail());
            viewModel.validatePassword(viewModel.getAuthData().getValue().getPassword());

            // If there are no validation errors, proceed with the login process
            AuthValidationState state = viewModel.getValidationState().getValue();
            if (state != null && state.isHasNoError()) {
                showLoadingProgress(); // Show animation loading dialog

                String email = authData.getEmail();
                String password = authData.getPassword();
                firebaseAuth
                        .signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener(
                                authResult -> {
                                    loadingDialog.dismiss();
                                    navigate(); // Navigate to the next screen
                                })
                        .addOnFailureListener(
                                e -> {
                                    loadingDialog.dismiss();
                                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                        Timber.e(
                                                "Sign in with email failed, invalid credentials: %s",
                                                e.getMessage());
                                    } else {
                                        Timber.e("Sign in with email failed: %s", e.getMessage());
                                    }

                                    Toast.makeText(
                                                    requireContext(),
                                                    "Sign in failed: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT)
                                            .show();
                                });
            }
        }
    }

    private void navigate() {
        NavOptions popUpSignInFragment =
                new NavOptions.Builder().setPopUpTo(R.id.signInPrimaryFragment, true).build();
        NavHostFragment.findNavController(this)
                .navigate(R.id.homeFragment, null, popUpSignInFragment);
    }

    private void showLoadingProgress() {
        loadingDialog.show(
                getParentFragmentManager(),
                SignInWithEmailFragment.this.getClass().getSimpleName());
    }
}
