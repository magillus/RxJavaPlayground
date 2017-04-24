package com.example.mat.rxutil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import timber.log.Timber;

/**
 * Class for Wrapping a Bound Service connection with subscription.
 * It returns IBinder which can be used to get a instance of Messenger for communication.
 * When Subscription is disposed it will disconnect
 * Copyright 2017 Mateusz Perlak - http://www.apache.org/licenses/LICENSE-2.0
 */
public class RxBoundService implements ObservableOnSubscribe<IBinder> {
    /**
     * Reference for context used to bind and unbind service.
     */
    private final WeakReference<Context> contextWeakReference;
    /**
     * Intent to the bound Serivce.
     */
    private final Intent serviceIntent;
    /**
     * Bind start service flag.
     */
    private final int serviceStartFlags;

    /**
     * Creates Service observable.
     * Binds to the service on first subscription and gives observable of IBinder, from the binder it depends on Service how it is handled.
     * Uses default {@link Context#BIND_AUTO_CREATE} bind flag.
     *
     * @param context       Context to start and bind to service.
     * @param serviceIntent Intent for Service to connect with.
     * @return
     */
    public static Observable<IBinder> create(Context context, Intent serviceIntent) {
        return Observable.defer(() -> Observable.create(new RxBoundService(context, serviceIntent)));
    }

    /**
     * Creates Service observable.
     * Binds to the service on first subscription and gives observable of IBinder, from the binder it depends on Service how it is handled.
     *
     * @param context       Context to start and bind to service.
     * @param serviceIntent Intent for Service to connect with.
     * @param flags         bind service flag paramter.
     * @return
     */
    public static Observable<IBinder> create(Context context, Intent serviceIntent, int flags) {
        return Observable.defer(() -> Observable.create(new RxBoundService(context, serviceIntent, flags)));
    }

    /**
     * Creates instance of RxBoundService.
     * Uses {@link Context#BIND_DEBUG_UNBIND}
     * @param context
     * @param serviceIntent
     */
    private RxBoundService(Context context, Intent serviceIntent) {
        this.serviceIntent = serviceIntent;
        contextWeakReference = new WeakReference<>(context);
        this.serviceStartFlags = Context.BIND_AUTO_CREATE;
    }

    /**
     * Creats instance of RxBoundSerivce.
     * @param context
     * @param serviceIntent
     * @param serviceStartFlags
     */
    private RxBoundService(Context context, Intent serviceIntent, int serviceStartFlags) {
        this.serviceIntent = serviceIntent;
        contextWeakReference = new WeakReference<>(context);
        this.serviceStartFlags = serviceStartFlags;
    }

    /**
     * Called on subscription. To have only one connection please use {@link Observable#share()} method.
     * @param emitter emitter for IBinder object
     * @throws Exception
     */
    @Override
    public synchronized void subscribe(ObservableEmitter<IBinder> emitter) throws Exception {
        //bind to service
        if (contextWeakReference.get() != null) {
            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    try {
                        if (binder != null) {
                            emitter.onNext(binder);
                        } else {
                            emitter.onError(new NullPointerException("Service returned with binder is NULL."));
                        }
                    } catch (Exception e) {
                        Timber.w(e, "Error fetching service from IBinder. when binding service");
                        emitter.onError(e);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    emitter.onComplete();
                }
            };

            emitter.setDisposable(Disposables.fromRunnable(() -> {
                Timber.i("Unbinding Rx base Service");
                contextWeakReference.get().unbindService(serviceConnection);
            }));

            try {
                boolean bound = contextWeakReference.get().bindService(serviceIntent, serviceConnection, serviceStartFlags);
                if (!bound) {
                    Timber.w("Service with intent %s NOT bounded", serviceIntent);
                    emitter.onError(new RuntimeException());
                }
            } catch (Exception ex) {
                // else skip error
                Timber.w(ex, "Error on binding service.");
                emitter.onError(ex);
            }
        } else {
            emitter.onError(new NullPointerException("Context from weakreference is null."));
        }
    }
}
