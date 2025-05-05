package com.optlab.banhangso.ui.authentication.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.optlab.banhangso.R;

import java.util.concurrent.TimeUnit;

public class FirebaseAuthProvider {
    private final Context context;
    private final FirebaseAuth firebaseAuth;

    public FirebaseAuthProvider(@NonNull Context context, @NonNull FirebaseAuth firebaseAuth) {
        this.context = context;
        this.firebaseAuth = firebaseAuth;
    }

    private String formatPhoneNumber(@NonNull String phone) {
        if (phone.length() == 10 && phone.charAt(0) == '0') {
            return context.getString(R.string.vietnam_phone_number_prefix) + phone.substring(1);
        }
        if (phone.length() == 12 && phone.startsWith("84")) {
            return context.getString(R.string.vietnam_phone_number_prefix) + phone.substring(2);
        }
        return phone;
    }

    public PhoneAuthOptions getPhoneAuthOption(
            @NonNull String phone,
            @NonNull PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        return PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(formatPhoneNumber(phone)) // Phone number to verify
                .setTimeout(120L, TimeUnit.SECONDS) // Timeout duration
                .setCallbacks(callbacks)
                .build();
    }
}
