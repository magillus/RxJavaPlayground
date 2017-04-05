package com.example.mat.rxjavaplayground.utils;

import timber.log.Timber;

/**
 * Created on 12/16/16.
 */

public class ThreadLogging {

    public static void log(String message) {

        Timber.i("TL: %s(%d): %s", Thread.currentThread().getName(), Thread.currentThread().getId(), message);
    }
}
