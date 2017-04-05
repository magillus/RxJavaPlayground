package com.example.mat.rxjavaplayground.service;

import android.os.SystemClock;

import com.example.mat.rxjavaplayground.utils.ThreadLogging;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created on 12/13/16.
 */

public class DataService {

    public Observable<String> getStringReapatedDefer(String name, int stringSize, long timeBetweenEmissions) {
        ThreadLogging.log("Creating Random String Observable (defer): "+name);
        return Observable.defer(() -> {
            ThreadLogging.log("Creating Random String Observable (inside defer)");
            RandomString randomString = new RandomString(stringSize);
            return Observable.interval(timeBetweenEmissions, TimeUnit.MILLISECONDS)
                    .map(tick -> {
                        ThreadLogging.log("Emitting value for '"+name+"' Random String Observable (defer)");
                        return randomString.nextString();
                    });
        });
    }

    public Observable<String> getStringRepeated(String name, int stringSize, long timeBetweenEmissions) {
        ThreadLogging.log("Creating Random String Observable: "+name);
        final RandomString randomString = new RandomString(stringSize);
        SystemClock.sleep(1000);// oh no, is this UI thread?
        return Observable.interval(timeBetweenEmissions, TimeUnit.MILLISECONDS)
                .map(tick -> {
                    ThreadLogging.log("Emitting value for '"+name+"' Random String Observable");
                    return randomString.nextString();
                });
    }
}
