package com.optlab.banhangso.ui.authentication.view;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.databinding.Observable;
import androidx.databinding.library.baseAdapters.BR;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavDirections;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.app.AuthData;
import com.optlab.banhangso.databinding.FragmentAuthenticationBinding;
import com.optlab.banhangso.ui.authentication.state.AuthValidationState;
import com.optlab.banhangso.ui.authentication.viewmodel.AuthenticationViewModel;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class AuthenticationFragment extends Fragment {
    private FragmentAuthenticationBinding binding;
    private AuthenticationViewModel authViewModel;
    private AnimationLoadingDialog loadingDialog;
    private boolean isSignIn = true;

    @Inject protected FirebaseAuth firebaseAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (firebaseAuth.getCurrentUser() != null) {
            navigateToHome();
        }
        loadingDialog = new AnimationLoadingDialog();
        initViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (loadingDialog != null && loadingDialog.isVisible()) {
            loadingDialog.dismiss();
        }
        binding = null;
    }

    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthenticationViewModel.class);
    }

    private void navigateToHome() {
        NavOptions popupOptions =
                new NavOptions.Builder().setPopUpTo(R.id.authenticationFragment, true).build();
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, popupOptions);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAuthenticationBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        binding.setViewModel(authViewModel);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().getWindow().setDecorFitsSystemWindows(false);
        ViewCompat.setOnApplyWindowInsetsListener(
                binding.getRoot(),
                (v, insets) -> {
                    Insets statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars());
                    ViewGroup.LayoutParams statusViewParams = binding.statusBar.getLayoutParams();
                    statusViewParams.height = statusBarInsets.top;
                    binding.statusBar.setLayoutParams(statusViewParams);
                    return insets;
                });

        setupAuthenticateButton();
        observeViewModel();
    }

    private void setupAuthenticateButton() {
        binding.mbAuthenticate.setOnClickListener(
                v -> {
                    if (isSignIn) {
                        loadingDialog.show(
                                getParentFragmentManager(), "loadingDialog_" + this.getClass().getSimpleName());
                        signInWithFirebaseEmail();
                    } else {
                        AuthData authData = authViewModel.getAuthData().getValue();
                        if (authData != null) {
                            NavDirections action =
                                    AuthenticationFragmentDirections.actionAuthenticationToSignUp(
                                            authData.getEmail(), authData.getPassword());
                            Navigation.findNavController(v).navigate(action);
                        }
                    }
                });
    }

    private void observeViewModel() {
        authViewModel.getAuthData().observe(getViewLifecycleOwner(), this::observeAuthData);

        authViewModel
                .getIsSignIn()
                .observe(getViewLifecycleOwner(), isSignIn -> this.isSignIn = isSignIn);
    }

    private void observeAuthData(AuthData authData) {
        authData.addOnPropertyChangedCallback(
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        AuthData authData = (AuthData) sender;
                        switch (propertyId) {
                            case BR.email -> authViewModel.validateEmail(authData.getEmail());
                            case BR.password ->
                                    authViewModel.validatePassword(authData.getPassword());
                            case BR.confirmPassword -> {
                                if (!isSignIn) {
                                    authViewModel.validateConfirmPassword(
                                            authData.getPassword(), authData.getConfirmPassword());
                                }
                            }
                        }
                    }
                });
    }

    private void showErrorMessage(String message) {
        if (getView() != null) {
            Toast.makeText(getContext(), message, LENGTH_LONG).show();
        }
    }

    /**
     * Signs in the user with Firebase using email and password.
     *
     * <p>This method retrieves the email and password from the authData object and attempts to sign
     * in the user using Firebase Authentication. If the sign-in is successful, it navigates to the
     * home screen. If it fails, it shows an error message.
     */
    private void signInWithFirebaseEmail() {
        AuthData authData = authViewModel.getAuthData().getValue();
        if (authData == null) {
            Timber.e("The authData is null, cannot sign in");
            showErrorMessage(getString(R.string.error_auth_data_null));
            loadingDialog.dismiss();
            return;
        }
        String email = authData.getEmail();
        String password = authData.getPassword();
        if (email == null || password == null) {
            Timber.e("Email or password is null");
            showErrorMessage(getString(R.string.error_email_password_null));
            loadingDialog.dismiss();
            return;
        }
        firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(
                        authResult -> {
                            loadingDialog.dismiss();
                            navigateToHome();
                        })
                .addOnFailureListener(
                        e -> {
                            Timber.e(e, "Failed to sign in with email and password");
                            showErrorMessage(getString(R.string.error_sign_in_failed));
                            loadingDialog.dismiss();
                        });
    }

    /**
     * Updates the UI based on the selected radio button in the RadioGroup.
     *
     * <p>This method changes the text color of the selected radio button to blue and the other to
     * gray. It also shows or hides the options layout based on the selected radio button.
     *
     * @param group     The RadioGroup containing the radio buttons.
     * @param checkedId The ID of the selected radio button.
     */
    public void onUserRoleSelected(RadioGroup group, int checkedId) {
        Context context = group.getContext();
        TypedValue typedValue = new TypedValue();
        context
                .getTheme()
                .resolveAttribute(com.google.android.material.R.attr.colorPrimary, typedValue, true);
        int blue = typedValue.data;
        int gray = ContextCompat.getColor(context, R.color.million_gray);

        if (checkedId == R.id.mrb_sign_in) {
            authViewModel.setIsSignIn(true);
            binding.mrbSignIn.setTextColor(blue);
            binding.mrbSignUp.setTextColor(gray);
            authViewModel.setValidateFields(AuthValidationState.SIGN_IN_EMAIL);
        } else {
            authViewModel.setIsSignIn(false);
            binding.mrbSignIn.setTextColor(gray);
            binding.mrbSignUp.setTextColor(blue);
            authViewModel.setValidateFields(AuthValidationState.SIGN_UP_EMAIL);
        }
    }

    public void onGoogleSignInButtonClick(View view) {
        GetCredentialRequest request =
                new GetCredentialRequest.Builder()
                        .addCredentialOption(getGetGoogleIdOption())
                        .build();

        Context activityContext = requireActivity();
        CredentialManager credentialManager = CredentialManager.create(activityContext);
        credentialManager.getCredentialAsync(
                activityContext,
                request,
                null,
                ContextCompat.getMainExecutor(activityContext),
                new CredentialManagerCallback<>() {
                    @Override
                    public void onResult(GetCredentialResponse getCredentialResponse) {
                        handleSignIn(getCredentialResponse);
                    }

                    @Override
                    public void onError(@NonNull GetCredentialException e) {
                        handleFailure(e);
                    }
                });
    }

    private void handleFailure(GetCredentialException e) {
        if (e instanceof NoCredentialException) {
            Timber.w("No credentials available. Prompt user to sign in manually.");
            // Show a message or redirect to manual sign-in
        } else {
            Timber.e(e, "Failed to get credential");
            Toast.makeText(
                            requireContext(),
                            "Google Sign-In failed. Please try again later or use email/password.",
                            Toast.LENGTH_LONG)
          .show();
        }
    }

    private void handleSignIn(GetCredentialResponse getCredentialResponse) {
        Credential credential = getCredentialResponse.getCredential();

        if (credential instanceof CustomCredential customCredential
                && credential
                        .getType()
                        .equals(GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL)) {
            // Extract the Google ID token from the custom credential
            GoogleIdTokenCredential googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(customCredential.getData());
            // Sign in with Firebase using the Google ID token
            firebaseAuthWithGoogle(googleIdTokenCredential.getIdToken());
        } else {
            Timber.w("Credential is not a Google ID token credential");
        }
    }

    /**
     * Firebase authentication with Google ID token using Firebase Authentication SDK.
     *
     * @param idToken The Google ID token obtained from the credential.
     */
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        firebaseAuth
                .signInWithCredential(credential)
                .addOnSuccessListener(
                        authResult -> {
                            // Sign-in succeeded, update UI with the signed-in user's information
                            Timber.d("SignInWithCredential: success");
                            navigateToHome();
                        })
                .addOnFailureListener(
                        e -> {
                            // Sign-in failed, display an error message to the user
                            Timber.w(e, "SignInWithCredential: failure");
                        });
    }

    /** Creates a GetGoogleIdOption instance with the specified options. */
    @NonNull
    private GetGoogleIdOption getGetGoogleIdOption() {
        return new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false) // Set to false to always show account picker
                .setServerClientId(getString(R.string.web_client_id))
                .setAutoSelectEnabled(false) // Ensure auto-select is disabled to always show picker
        .build();
    }
}
