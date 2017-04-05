package com.example.mat.rxjavaplayground.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class RandomService extends Service {

    private RandomString randomString;

    public RandomService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        randomString = new RandomString(20);
    }

    public void setLength(int length) {
        randomString = new RandomString(length);
    }

    public Observable<String> randomStringObservable(int interval) {
        return Observable.defer(() ->
                Observable.merge(
                        Observable.just(0L),
                        Observable.interval(interval, TimeUnit.SECONDS))
                        .map(tick -> randomString.nextString()));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SelfBinder(this);
    }

    public static class SelfBinder extends Binder {
        final RandomService service;

        SelfBinder(RandomService service) {
            this.service = service;
        }

        public RandomService getService() {
            return this.service;
        }
    }
}
