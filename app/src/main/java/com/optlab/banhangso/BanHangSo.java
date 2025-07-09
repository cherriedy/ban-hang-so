package com.optlab.banhangso;

import android.app.Application;

import com.optlab.banhangso.internal.utilities.Logging;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class for BanHangSo app.
 */
@HiltAndroidApp
public class BanHangSo extends Application {

  @Override
  public void onCreate() {
    super.onCreate();

    // Initialize Timber for logging
    if (BuildConfig.DEBUG) {
      Logging.configTimber();
    }
  }
}
