package com.optlab.banhangso.internal.inteceptors;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

import io.reactivex.rxjava3.exceptions.Exceptions;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthenticationInterceptor implements Interceptor {

    private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

    @NonNull @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        if (firebaseUser == null) {
            return chain.proceed(request);
        }

        final Request[] authorizedRequest = new Request[1];
        firebaseUser
                .getIdToken(true)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                String authorization = "Bearer " + task.getResult().getToken();
                                authorizedRequest[0] =
                                        request.newBuilder()
                                                .addHeader("Authorization", authorization)
                                                .build();
                            }
                        });

        try {
            return chain.proceed(authorizedRequest[0]);
        } catch (IOException e) {
            throw Exceptions.propagate(e);
        }
    }
}
