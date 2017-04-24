package com.example.mat.rxutil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.lang.ref.WeakReference;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Emitter;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposables;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.schedulers.Schedulers;


/**
 * RxJava based broadcast reciever that registers its local BroadcastReceiver until end of subscription.
 * Listens for update and passes Intent to the Stream (Subscriber).
 * Copyright 2016 Mateusz Perlak - http://www.apache.org/licenses/LICENSE-2.0
 * Created on 11/18/16.
 */
public class RxBroadcastReceiver implements ObservableOnSubscribe<Intent> {
    protected final WeakReference<Context> contextWeakReference;

    private IntentFilter intentFilter;

    /**
     * Creates Observable with intent filter for Broadcast receiver.
     *
     * @param context
     * @param intentFilter
     * @return
     */
    public static Observable<Intent> create(Context context, IntentFilter intentFilter) {
        return Observable.defer(() -> Observable.create(new RxBroadcastReceiver(context, intentFilter))
                .subscribeOn(Schedulers.io())
        );
    }

    /**
     * @param context
     * @param intentFilter
     */
    private RxBroadcastReceiver(Context context, IntentFilter intentFilter) {
        contextWeakReference = new WeakReference<>(context.getApplicationContext());
        this.intentFilter = intentFilter;
    }

    @Override
    public void subscribe(ObservableEmitter<Intent> emitter) throws Exception {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                emitter.onNext(intent);
            }
        };
        emitter.setDisposable(Disposables.fromRunnable(() -> {
            if (contextWeakReference != null && contextWeakReference.get() != null) {
                contextWeakReference.get().unregisterReceiver(broadcastReceiver);
            }
        }));

        if (contextWeakReference != null && contextWeakReference.get() != null) {
            contextWeakReference.get().registerReceiver(broadcastReceiver, intentFilter);
        }
    }
}
