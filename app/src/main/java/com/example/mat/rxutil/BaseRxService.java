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
 * Created on 1/17/17.
 */

public abstract class BaseRxService<T> implements ObservableOnSubscribe<T> {
    private final WeakReference<Context> contextWeakReference;
    private final Intent serviceIntent;

    public static <T> Observable<T> create(Context context, Intent serviceIntent, Function<IBinder, T> getBoundInstance) {
        return Observable.defer(() -> {
            BaseRxService baseRxService = new BaseRxService(context, serviceIntent) {
                @Override
                protected Object getBoundService(IBinder binder) throws Exception {
                    return getBoundInstance.apply(binder);
                }
            };
            return Observable.create(baseRxService);
        });
    }

    private BaseRxService(Context context, Intent serviceIntent) {
        this.serviceIntent = serviceIntent;
        contextWeakReference = new WeakReference<>(context);
    }

    protected abstract T getBoundService(IBinder binder) throws Exception;

    @Override
    public synchronized void subscribe(ObservableEmitter<T> emitter) throws Exception {
        //bind to service
        if (contextWeakReference.get() != null) {
            ServiceConnection serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder binder) {
                    try {
                        T service = getBoundService(binder);
                        if (service != null) {
                            emitter.onNext(service);
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
                contextWeakReference.get().bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
            } catch (Exception ex) {
                // else skip error
                Timber.w(ex, "Error on binding service.");
                emitter.onError(ex);
            }
        } else {
            // throw error
            emitter.onError(new NullPointerException("Context is null."));
        }
    }
}
