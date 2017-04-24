package com.example.mat.rxjavaplayground.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.service.RandomService;
import com.example.mat.rxutil.RxBoundService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created on 1/18/17.
 */

public class RxServiceActivity extends BaseChildActivity {

    @BindView(R.id.txt_value10)
    TextView txt10;
    @BindView(R.id.txt_value20)
    TextView txt20;

    private Unbinder unbinder;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_sample);
        unbinder = ButterKnife.bind(this);

    }

    @Override
    protected void onResume() {
        super.onResume();

        Observable<RandomService> randomServiceObservable =
                RxBoundService.create(this, new Intent(this, RandomService.class))
                        .map(binder-> ((RandomService.SelfBinder) binder).getService())
                        .share(); // we share and care

        compositeDisposable.add(
                randomServiceObservable
                        .subscribeOn(Schedulers.io())
                        .flatMap(service -> service.randomStringObservable(10))
                        .doOnNext(msg-> Timber.i("message A: %s", msg))
                        .take(6)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(text-> txt10.setText(String.format("Random text every 10 seconds: %s", text))));

        compositeDisposable.add(
                randomServiceObservable
                        .subscribeOn(Schedulers.io())
                        .flatMap(service -> service.randomStringObservable(20))
                        .doOnNext(msg-> Timber.i("message B: %s", msg))
                        .take(3)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(text-> txt20.setText(String.format("Random text every 20 seconds: %s", text))));
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
