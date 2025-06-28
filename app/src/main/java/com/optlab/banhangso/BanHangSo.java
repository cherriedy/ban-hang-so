package com.optlab.banhangso;

import android.app.Application;
import dagger.hilt.android.HiltAndroidApp;
import timber.log.Timber;

/**
 * Application class for BanHangSo app. Handles global app initialization including Firebase
 * configurations.
 */
@HiltAndroidApp
public class BanHangSo extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        //        // Configure Firebase Firestore with offline persistence
        //        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        //        FirebaseFirestoreSettings settings =
        //                new FirebaseFirestoreSettings.Builder()
        //                        .setPersistenceEnabled(true)
        //                        .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
        //                        .build();
        //
        //        firestore.setFirestoreSettings(settings);
        //        Timber.d("Firebase Firestore initialized with offline persistence enabled");
    }
}
