package com.optlab.banhangso.ui.login.view;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.credentials.exceptions.NoCredentialException;
import androidx.fragment.app.Fragment;

import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentLoginBinding;
import com.optlab.banhangso.ui.adapter.LoginViewPagerAdapter;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class LoginFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;

    private FragmentLoginBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigate(firebaseAuth.getCurrentUser());
    }

    private void navigate(FirebaseUser currentUser) {}

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        binding.setLifecycleOwner(this);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.sibGoogle.setOnClickListener(this::onGoogleSignInClick);
        initViewPager();
        setupTabLayout();
    }

    private void setupTabLayout() {
        int[] tabResId = {R.string.phone_number};

        new TabLayoutMediator(
                binding.tlMethod,
                binding.vpAccount,
                (tab, position) -> {
                    if (position >= 0 && position <= tabResId.length) {
                        tab.setText(tabResId[position]);
                    }
                }).attach();
    }

    private void initViewPager() {
        LoginViewPagerAdapter adapter = new LoginViewPagerAdapter(this);
        binding.vpAccount.setAdapter(adapter);
        binding.vpAccount.setUserInputEnabled(false); // Disable swipe to change tabs
    }

    public void onGoogleSignInClick(@NonNull View view) {
        // Create a GetCredentialRequest to request the Google ID credential option and specify
        // the options for the request.
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
                            navigate(authResult.getUser());
                        })
                .addOnFailureListener(
                        e -> {
                            // Sign-in failed, display an error message to the user
                            Timber.w(e, "SignInWithCredential: failure");
                            navigate(null);
                        });
    }

    /** Creates a GetGoogleIdOption instance with the specified options. */
    @NonNull
    private GetGoogleIdOption getGetGoogleIdOption() {
        return new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(true) // Filter by authorized accounts if needed
                .setServerClientId(getString(R.string.web_client_id)) // Set your server client ID
                .setAutoSelectEnabled(false) // Enable auto-select if desired
                .build();
    }
}
