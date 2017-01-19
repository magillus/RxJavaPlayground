package com.example.mat.rxjavaplayground;

import android.app.Application;

import timber.log.Timber;

/**
 * Created on 12/16/16.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}
