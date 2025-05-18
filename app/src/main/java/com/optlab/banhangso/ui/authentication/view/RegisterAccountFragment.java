package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.optlab.banhangso.NavGraphSignUpDirections;
import com.optlab.banhangso.R;
import com.optlab.banhangso.data.model.AuthData;
import com.optlab.banhangso.data.model.User;
import com.optlab.banhangso.data.repository.UserRepository;
import com.optlab.banhangso.databinding.FragmentRegisterAccountBinding;
import com.optlab.banhangso.ui.authentication.common.FirebaseAuthProvider;
import com.optlab.banhangso.ui.authentication.viewmodel.SignUpViewModel;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;

import dagger.hilt.android.AndroidEntryPoint;

import timber.log.Timber;

import javax.inject.Inject;

@AndroidEntryPoint
public class RegisterAccountFragment extends Fragment {
    private FragmentRegisterAccountBinding binding;
    private SignUpViewModel viewModel;
    private AnimationLoadingDialog loadingDialog;

    @Inject protected FirebaseAuth firebaseAuth;
    @Inject protected UserRepository repository;
    @Inject protected FirebaseAuthProvider firebaseAuthProvider;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // firebaseAuth.getFirebaseAuthSettings().setAppVerificationDisabledForTesting(true);
        loadingDialog = new AnimationLoadingDialog();
        initViewModel();
    }

    private void initViewModel() {
        NavBackStackEntry navBackStackEntry =
                NavHostFragment.findNavController(this).getBackStackEntry(R.id.nav_graph_sign_up);
        viewModel = new ViewModelProvider(navBackStackEntry).get(SignUpViewModel.class);
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
        setUpAdditionalContactMethod();
    }

    private void setUpAdditionalContactMethod() {
        AuthData authData = viewModel.getAuthData().getValue();
        if (authData == null) {
            return;
        }

        String phoneNumber = authData.getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            binding.tvAdditionalContactMethod.setText(getString(R.string.email));
            binding.tieAdditionalContactMethod.setHint(getString(R.string.hint_email));
            binding.tieAdditionalContactMethod.setInputType(
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS | InputType.TYPE_CLASS_TEXT);
        }

        String emailAddress = authData.getEmail();
        if (!TextUtils.isEmpty(emailAddress)) {
            binding.tvAdditionalContactMethod.setText(getString(R.string.phone_number));
            binding.tieAdditionalContactMethod.setHint(getString(R.string.hint_phone_number));
            binding.tieAdditionalContactMethod.setInputType(InputType.TYPE_CLASS_PHONE);
        }

        binding.tieAdditionalContactMethod.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable s) {
                        User user = viewModel.getUser().getValue();
                        if (user != null) {
                            String value = s.toString();
                            if (!TextUtils.isEmpty(phoneNumber)) {
                                user.setPhone(value);
                            } else if (!TextUtils.isEmpty(emailAddress)) {
                                user.setEmail(value);
                            }
                            viewModel.setUser(user);
                        }
                    }
                });
    }

    /**
     * Called when the user clicks the "Complete" button.
     *
     * <p>* This method is responsible for handling the sign-up process. It retrieves the
     * authentication data from the ViewModel and initiates the sign-up process using either phone
     * number or email address, depending on the provided information.
     */
    public void onCompleteButtonClick(@NonNull View view) {
        AuthData authData = viewModel.getAuthData().getValue();
        if (authData == null) {
            Timber.e("onCompleteButtonClick: AuthData is null");
            return;
        }

        loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());

        // Sign up with phone number
        String phoneNumber = authData.getPhoneNumber();
        if (!TextUtils.isEmpty(phoneNumber)) {
            PhoneAuthProvider.verifyPhoneNumber(
                    firebaseAuthProvider.getPhoneAuthOption(
                            phoneNumber, getPhoneAuthCallbacks(phoneNumber)));
        }

        // Sign up with email address
        String email = authData.getEmail();
        String password = authData.getPassword();
        Timber.d("onCompleteButtonClick: Email: %s, Password: %s", email, password);
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {
            firebaseAuth
                    .createUserWithEmailAndPassword(email, password) // Create user with email
                    .addOnSuccessListener(this::onSignUpWithEmailSuccess) // Sign up success
                    .addOnFailureListener(this::onSignUpWithEmailFailure); // Sign up failure
        } else {
            loadingDialog.dismiss();
            Timber.e("onCompleteButtonClick: Email or password is empty");
            Toast.makeText(
                            requireContext(),
                            R.string.email_or_password_is_empty,
                            Toast.LENGTH_SHORT)
                    .show();
        }
    }

    /** Called when the sign-up with email fails. */
    private void onSignUpWithEmailFailure(@NonNull Exception e) {
        loadingDialog.dismiss();
        Timber.e("onSignUpWithEmailFailure: %s", e.getMessage());
        Toast.makeText(requireContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the sign-up with email is successful.
     *
     * <p>This method is responsible for creating a new user in the database after a successful
     * sign-up with email. It retrieves the Firebase user and creates a new User object with the
     * user's information. Then, it calls the repository to create the user in the database.
     *
     * @param authResult The result of the sign-up operation, containing the Firebase user.
     */
    private void onSignUpWithEmailSuccess(@NonNull AuthResult authResult) {
        FirebaseUser firebaseUser = authResult.getUser();
        if (firebaseUser != null) {
            User newUser = new User();
            newUser.setId(firebaseUser.getUid());
            newUser.setEmail(firebaseUser.getEmail());
            newUser.setContactName(""); // Mock data
            repository.createUser(
                    newUser,
                    result -> {
                        if (Boolean.TRUE.equals(result)) {
                            onCreateUserSuccess();
                        } else {
                            onCreateUserFailure();
                        }
                    });
        }
    }

    /** Called when the user is successfully created in the database. */
    private void onCreateUserSuccess() {
        loadingDialog.dismiss();
        Toast.makeText(requireContext(), R.string.sign_up_successful, Toast.LENGTH_SHORT).show();
        // Pop up all fragments in the back stack until the signInPrimaryFragment
        NavOptions navOptions =
                new NavOptions.Builder().setPopUpTo(R.id.signInPrimaryFragment, true).build();
        NavHostFragment.findNavController(this).navigate(R.id.homeFragment, null, navOptions);
    }

    /** Called when the user creation in the database fails. */
    private void onCreateUserFailure() {
        loadingDialog.dismiss();
        Timber.e("onCreateUserFailure: Failed to create user in the database");
        Toast.makeText(this.requireContext(), R.string.sign_up_failed, Toast.LENGTH_SHORT).show();
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks getPhoneAuthCallbacks(
            @NonNull String phoneNumber) {
        return new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loadingDialog.dismiss();
                Timber.d("onVerificationCompleted: %s", phoneAuthCredential.getSmsCode());
                signUpWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                loadingDialog.dismiss();
                Timber.e("onVerificationFailed: %s", e.getMessage());
            }

            @Override
            public void onCodeSent(
                    @NonNull String verificationId,
                    @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                loadingDialog.dismiss();
                Timber.d("onCodeSent: %s", verificationId);
                navigateToVerificationFragment(phoneNumber, verificationId);
            }
        };
    }

    private void navigateToVerificationFragment(String phoneNumber, String verificationId) {
        NavDirections action =
                NavGraphSignUpDirections.actionRegisterAccountToOtpVerification(
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
