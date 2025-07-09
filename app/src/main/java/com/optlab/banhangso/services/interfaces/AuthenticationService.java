package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.UserFirebaseObject;
import com.optlab.banhangso.models.remote.requestes.SignUpRequest;
import com.optlab.banhangso.models.remote.responses.base.Response;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

  @POST("auth/signup")
  Single<Response<UserFirebaseObject>> signUpWithEmailAndPassword(
      @Body SignUpRequest signUpRequest);
}
