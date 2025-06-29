package com.optlab.banhangso.internal.injector;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.services.FirebaseAuthServiceImpl;
import com.optlab.banhangso.services.FirebaseStoreServiceImpl;
import com.optlab.banhangso.services.FirebaseUserServiceImpl;
import com.optlab.banhangso.services.interfaces.FirebaseAuthService;
import com.optlab.banhangso.services.interfaces.FirebaseStoreService;
import com.optlab.banhangso.services.interfaces.FirebaseUserService;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;
import org.jetbrains.annotations.Contract;

@Module
@InstallIn(SingletonComponent.class)
public abstract class FirebaseModule {

  private FirebaseModule() {}

  @NonNull @Provides
  @Singleton
  public static FirebaseFirestore provideFirebaseFirestore() {
    return FirebaseFirestore.getInstance();
  }

  @NonNull @Provides
  @Singleton
  public static FirebaseAuth provideFirebaseAuth() {
    return FirebaseAuth.getInstance();
  }

  @NonNull @Contract(value = "_ -> new", pure = true)
  @Provides
  @Singleton
  public static FirebaseStoreService provideFirebaseStoreService(FirebaseFirestore firestore) {
    return new FirebaseStoreServiceImpl(firestore);
  }

  @NonNull @Contract(value = "_ -> new", pure = true)
  @Provides
  @Singleton
  public static FirebaseUserService provideFirebaseUserService(FirebaseFirestore firestore) {
    return new FirebaseUserServiceImpl(firestore);
  }

  @NonNull @Contract(value = "_ -> new", pure = true)
  @Provides
  @Singleton
  public static FirebaseAuthService provideFirebaseAuthService(FirebaseAuth firebaseAuth) {
    return new FirebaseAuthServiceImpl(firebaseAuth);
  }
}
