package com.optlab.banhangso.services.interfaces;

import com.optlab.banhangso.models.remote.UserFirebaseObject;
import com.optlab.banhangso.models.remote.render_api.ResponseObject;
import com.optlab.banhangso.models.remote.render_api.SignUpRequestObject;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthenticationService {

  @POST("auth/signup")
  Single<ResponseObject<UserFirebaseObject>> signUpWithEmailAndPassword(
      @Body SignUpRequestObject signUpRequestObject);
}
