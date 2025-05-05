package com.optlab.banhangso.ui.authentication.view;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.optlab.banhangso.R;
import com.optlab.banhangso.databinding.FragmentVerificationOtpBinding;
import com.optlab.banhangso.ui.common.view.AnimationLoadingDialog;

import dagger.hilt.android.AndroidEntryPoint;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

@AndroidEntryPoint
public class OtpVerificationFragment extends Fragment {
    @Inject protected FirebaseAuth firebaseAuth;

    private FragmentVerificationOtpBinding binding;
    private OtpVerificationFragmentArgs args;
    private AnimationLoadingDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadingDialog = new AnimationLoadingDialog();
        setupNavigation();
    }

    private void setupNavigation() {
        args = OtpVerificationFragmentArgs.fromBundle(requireArguments());
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentVerificationOtpBinding.inflate(inflater, container, false);
        binding.setFragment(this);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.tiePhone.setText(args.getPhoneNumber());
        initToolBar();
        setupOtpInputs();
        setupCountdownTimer();
        binding.tieOpt6.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(s)) { // Enable the button if the OTP is not empty
                            // Enable the button if the OTP is not empty
                            binding.mbNext.setEnabled(true);
                        } else {
                            // Disable the button if the OTP is empty
                            binding.mbNext.setEnabled(false);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
    }

    private void initToolBar() {}

    /** Set up the countdown timer for OTP verification. */
    private void setupCountdownTimer() {
        // Set up a countdown timer for 2 minutes, with 1-second intervals.
        CountDownTimer countDownTimer =
                new CountDownTimer(1000 * 60 * 2L, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // Disable the resend OTP button and set the countdown timer text.
                        binding.mbResendOtp.setEnabled(false);
                        // Set the color for the resend OTP button before starting to count.
                        binding.mbResendOtp.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.raven));

                        String timeRemaining =
                                String.format(
                                        Locale.getDefault(),
                                        "%02d:%02d", // Format for minutes and seconds
                                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);

                        // Update the countdown timer text view with the remaining time.
                        binding.tvCountdown.setText(timeRemaining);
                    }

                    @Override
                    public void onFinish() {
                        // Set the countdown timer text to "00:00" when finished.
                        binding.tvCountdown.setText(R.string.default_countdown);

                        // Enable the resend OTP button when the countdown is finished.
                        binding.mbResendOtp.setEnabled(true);
                        // Set the color for the resend OTP button when the countdown is finished.
                        binding.mbResendOtp.setTextColor(
                                ContextCompat.getColor(requireContext(), R.color.boston_blue));
                    }
                };

        countDownTimer.start(); // Start the countdown timer.
    }

    private void setupOtpInputs() {
        requestFocus(binding.tieOpt1, binding.tieOpt2);
        requestFocus(binding.tieOpt2, binding.tieOpt3);
        requestFocus(binding.tieOpt3, binding.tieOpt4);
        requestFocus(binding.tieOpt4, binding.tieOpt5);
        requestFocus(binding.tieOpt5, binding.tieOpt6);
    }

    private void requestFocus(TextInputEditText firstInput, TextInputEditText secondInput) {
        firstInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Move focus to the next input field if the current one is not empty.
                        if (!TextUtils.isEmpty(s)) {
                            // Move the current input field to the next one.
                            secondInput.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });

        secondInput.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(
                            CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Move focus to the previous input field if the current one is empty.
                        if (TextUtils.isEmpty(s)) {
                            // Move the current input field to the previous one.
                            firstInput.requestFocus();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {}
                });
    }

    /**
     * @noinspection unused
     */
    public void onNextButtonClick(@NonNull View view) {
        String otp = getOtpFromInputs();
        String verificationId = args.getVerificationId();

        if (!TextUtils.isEmpty(verificationId) && !TextUtils.isEmpty(otp)) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            signInWithPhoneAuthCredential(credential);
            loadingDialog.show(getParentFragmentManager(), this.getClass().getSimpleName());
        }
    }

    /**
     * @noinspection unused
     */
    public void onResendOtpButtonClick(@NonNull View view) {
        setupCountdownTimer();
        // String phone = args.getPhoneNumber();
        // String verificationId = args.getVerificationId();
        //
        // if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(verificationId)) {
        //     resendVerificationCode(phone, verificationId);
        // }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth
                .signInWithCredential(credential)
                .addOnSuccessListener(
                        authResult -> {
                            loadingDialog.dismiss();
                            NavOptions popUpLoginFragment =
                                    new NavOptions.Builder()
                                            .setPopUpTo(R.id.otpVerificationFragment, true)
                                            .build();
                            NavHostFragment.findNavController(this)
                                    .navigate(R.id.homeFragment, null, popUpLoginFragment);
                        })
                .addOnFailureListener(
                        e -> {
                            loadingDialog.dismiss();
                            Toast.makeText(requireContext(), "OTP is invalid", Toast.LENGTH_SHORT)
                                    .show();
                        });
    }

    /**
     * @noinspection DataFlowIssue
     */
    private String getOtpFromInputs() {
        return binding.tieOpt1.getText().toString()
                + binding.tieOpt2.getText().toString()
                + binding.tieOpt3.getText().toString()
                + binding.tieOpt4.getText().toString()
                + binding.tieOpt5.getText().toString()
                + binding.tieOpt6.getText().toString();
    }
}
