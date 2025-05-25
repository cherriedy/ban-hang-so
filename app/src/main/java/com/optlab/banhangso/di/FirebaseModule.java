package com.optlab.banhangso.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.optlab.banhangso.data.remote.service.FirebaseStoreService;
import com.optlab.banhangso.data.remote.service.FirebaseUserService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class FirebaseModule {
    @Provides
    @Singleton
    public static FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

    @Provides
    @Singleton
    public static FirebaseStoreService provideFirebaseStoreService(FirebaseFirestore firestore) {
        return new FirebaseStoreService(firestore);
    }

    @Provides
    @Singleton
    public static FirebaseUserService provideFirebaseUserService(FirebaseFirestore firestore) {
        return new FirebaseUserService(firestore);
    }
}
