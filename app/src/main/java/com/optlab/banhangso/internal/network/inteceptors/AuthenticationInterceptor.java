package com.optlab.banhangso.internal.network.inteceptors;

import androidx.annotation.NonNull;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import timber.log.Timber;

public class AuthenticationInterceptor implements Interceptor {

  private final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

  @NonNull @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    Request request = chain.request();

    // If no user is logged in, proceed with original request
    if (firebaseUser == null) {
      return chain.proceed(request);
    }

    try {
      String token =
          Single.fromCallable(() -> Tasks.await(firebaseUser.getIdToken(true)).getToken())
              .subscribeOn(Schedulers.io())
              .blockingGet();

      String authorization = "Bearer " + token;
      Request authorizedRequest =
          request.newBuilder().addHeader("Authorization", authorization).build();

      return chain.proceed(authorizedRequest);

    } catch (Exception e) {
      Timber.e(e, "Failed to get ID token, using original request");
      // If token retrieval fails, proceed with original request
      return chain.proceed(request);
    }
  }
}
