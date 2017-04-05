package com.example.mat.rxutil;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;

import java.lang.ref.WeakReference;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Subscriber that registers and un-registers to/from content resolver itself as observer.
 * On Subscription it will register observer based on passed 'observer' and unregister with un-subscribe.
 * Copyright 2016 Mateusz Perlak - http://www.apache.org/licenses/LICENSE-2.0
 */
public abstract class ContentObserverSubscriber<T> implements ObservableOnSubscribe<T> {

    /**
     * Weak reference to content resolver
     */
    protected final WeakReference<ContentResolver> contentResolverRef;
    /**
     * Observed URI.
     */
    protected final Uri observedUri;
    /**
     * Handler thread on which the observed changes will happen.
     */
    protected HandlerThread handlerThread;
    /**
     * Emits first value if true.
     */
    protected boolean emitFirstValue = true;

    /**
     * Flag if the content observer was unregistered.
     */
    private volatile boolean isObserverRegistered = false;

    /**
     * Creates content observer observable. It will emit on any changes of the content and also emit first value.
     *
     * @param resolver       ContentResolver instance for registration/un-registration
     * @param observedUri    Observed content URI
     * @param fetchStatusFun method to fetch a type from content provider or from the source
     * @param <T>            Type of the Observable
     * @return
     */
    public static <T> Observable<T> create(ContentResolver resolver, Uri observedUri, Function<Uri, T> fetchStatusFun) {
        return Observable.defer(() -> {
            ContentObserverSubscriber<T> contentObserverSubscriber = new ContentObserverSubscriber<T>(resolver, observedUri) {
                @Override
                protected T fetchItem(Uri itemUri) {
                    try {
                        return fetchStatusFun.apply(itemUri);
                    } catch (Exception e) {
                        Timber.e(e, "Error fetching item");
                        return null;
                    }
                }
            };
            return Observable.create(contentObserverSubscriber);
        });
    }

    /**
     * Subscriber subscription call.
     * It will store subscriber for later emits.
     * It will emit first value if it was configured for this.
     * It will register observer.
     *
     * @param emitter
     */
    @Override
    public void subscribe(ObservableEmitter<T> emitter) throws Exception {
        if (emitFirstValue) {
            T item = fetchItem(observedUri);
            if (item != null) {
                emitter.onNext(item);
            }
        }
        try {
            if (contentResolverRef != null && contentResolverRef.get() != null) {

                ContentObserver contentObserver = new ContentObserver(new Handler(handlerThread.getLooper())) {
                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        if (!selfChange) {
                            T item = fetchItem(uri);
                            if (item != null) {
                                emitter.onNext(item);
                            }
                        }
                    }
                };

                contentResolverRef.get().registerContentObserver(observedUri, true, contentObserver);
                Timber.d("Registering observer on Uri: %s", observedUri);
                isObserverRegistered = true;

                // setup dispose action to un-register observer
                emitter.setDisposable(Disposables.fromRunnable(() -> {
                    if (contentResolverRef != null && contentResolverRef.get() != null && isObserverRegistered) {
                        contentResolverRef.get().unregisterContentObserver(contentObserver);
                        Timber.d("Un-registering observer on Uri: %s", observedUri);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            handlerThread.quitSafely();
                        } else {
                            handlerThread.quit();
                        }
                        handlerThread = null;
                    }
                    isObserverRegistered = false;
                }));

            }
        } catch (Exception ex) {
            Timber.w(ex, "Error registering observer for URI %s", observedUri);
            emitter.onError(ex);
        }
    }

    /**
     * Creates instance of {@link ContentObserverSubscriber} for observed Uri.
     * It will emit first item on subscription.
     *
     * @param resolver
     * @param observedUri observed Uri
     */
    public ContentObserverSubscriber(ContentResolver resolver, Uri observedUri) {
        this(resolver, observedUri, true);
    }

    /**
     * Creates instance of {@link ContentObserverSubscriber} for observed Uri.
     * it may emit first value on subscription if {@param emitFirstValue} is true.
     *
     * @param resolver
     * @param observedUri    observed Uri
     * @param emitFirstValue true will emit first value on subscription
     */
    public ContentObserverSubscriber(ContentResolver resolver, Uri observedUri, boolean emitFirstValue) {
        this.emitFirstValue = emitFirstValue;
        this.contentResolverRef = new WeakReference<>(resolver);
        this.observedUri = observedUri;
        handlerThread = new HandlerThread("ContentObserverThread");
        handlerThread.start();
    }

    /**
     * Method that should return an item for Uri to send emit
     *
     * @return
     */
    protected abstract T fetchItem(Uri changeUri);

}
