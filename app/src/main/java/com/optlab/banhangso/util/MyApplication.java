package com.optlab.banhangso.util;

import android.app.Application;

import com.optlab.banhangso.util.timber.TimberUtils;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TimberUtils.configTimber();
    }
}
