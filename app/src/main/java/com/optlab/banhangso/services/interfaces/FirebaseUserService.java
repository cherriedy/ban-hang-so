package com.optlab.banhangso.services.interfaces;

import androidx.annotation.NonNull;
import com.optlab.banhangso.models.remote.UserFirebaseObject;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Deprecated
public interface FirebaseUserService {
  String USERS_COLLECTION = "users";

  Single<UserFirebaseObject> setUser(@NonNull UserFirebaseObject userFirebaseObject);

  Maybe<UserFirebaseObject> getUser(@NonNull String userId);
}
