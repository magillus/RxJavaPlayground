package com.example.mat.rxjavaplayground.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mat.rxjavaplayground.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class SinglesObservableActivity extends AppCompatActivity {

    @BindView(R.id.lbl_disposable_state)
    TextView disposableState;
    @BindView(R.id.lbl_message)
    TextView messageText;

    Disposable mainDisposable;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singles_observable);
        ButterKnife.bind(this);
        messageText.setSingleLine(false);
    }

    @OnClick(R.id.btn_dispose)
    public void disposeSubscription(View v) {
        if (mainDisposable != null) {
            mainDisposable.dispose();
        }
    }

    @OnClick(R.id.btn_create_subscription)
    public void createSubscription(View v) {
        mainDisposable = Single.merge(
                Single.just("First single"), Observable.fromArray("One", "Two", "Three").single("Default"))
                .doOnComplete(() -> log("Merge on complete"))
                .doOnNext((item)-> log("Merge on next: "+item))
                .doFinally(()-> log("Merge on finally"))
                .debounce(2, TimeUnit.SECONDS)
                .take(1)
                .single("default")
                .doOnSuccess((item) -> log("SIngle onsuccess: "+item))
                .doOnSubscribe((d)-> log("Single on subscribe: "))
                .doOnDispose(()->log("Single on dispose"))
                .doFinally(()-> log("Single on finally"))
                .map(msg -> String.format("Single merge: %s", msg))
                .subscribe(message -> {

                    log(message);
                });
    }

    public void log(String message) {
        Single.fromCallable(() -> message)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(msg-> {
                    messageText.setText(messageText.getText() + "\n" + msg);
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable.add(Observable.interval(100, TimeUnit.MILLISECONDS)
                .map(tick -> {
                    if (mainDisposable != null) {
                        return String.format("Main disposable is %s", mainDisposable.isDisposed() ? "Disposed" : "Not disposed");
                    } else {
                        return "Main disposable null.";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(disposableState::setText));

    }


    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.clear();
    }
}
