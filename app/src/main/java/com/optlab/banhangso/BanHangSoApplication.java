package com.optlab.banhangso;

import android.app.Application;

import com.optlab.banhangso.util.Logging;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class BanHangSoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logging.configTimber();
    }
}
