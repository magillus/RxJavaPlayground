package com.example.mat.rxjavaplayground.activity;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.provider.TextContentProvider;
import com.example.mat.rxutil.ContentObserverSubscriber;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 1/3/17.
 */

public class ContentProviderActivity extends BaseChildActivity {

    @BindView(R.id.txt_value10)
    TextView value10;
    @BindView(R.id.txt_value20)
    TextView value20;

    private Unbinder unbinder;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_provider);
        unbinder = ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_init_random10)
    public void onNext10(View v) {
        getContentResolver().insert(TextContentProvider.getRandomStringUri(10), null);
    }

    @OnClick(R.id.btn_notify_change10)
    public void onNotify10(View v) {
        getContentResolver().notifyChange(TextContentProvider.getRandomStringUri(10), null);
    }

    @OnClick(R.id.btn_remove_random10)
    public void onRemove10(View v) {
        getContentResolver().delete(TextContentProvider.getRandomStringUri(10), null, null);
    }

    @OnClick(R.id.btn_init_random20)
    public void onNext20(View v) {
        getContentResolver().insert(TextContentProvider.getRandomStringUri(20), null);
    }

    @OnClick(R.id.btn_notify_change20)
    public void onNotify20(View v) {
        getContentResolver().notifyChange(TextContentProvider.getRandomStringUri(20), null);
    }

    @OnClick(R.id.btn_remove_random20)
    public void onRemove20(View v) {
        getContentResolver().delete(TextContentProvider.getRandomStringUri(20), null, null);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Observable<String> observable10 = ContentObserverSubscriber.create(getContentResolver(),
                TextContentProvider.getRandomStringUri(10),
                uri -> TextContentProvider.getRandomString(getContentResolver(), 10))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .share();// here we do share

        Observable<String> observable20 = ContentObserverSubscriber.create(getContentResolver(),
                TextContentProvider.getRandomStringUri(20),
                uri -> TextContentProvider.getRandomString(getContentResolver(), 20))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        compositeDisposable.add(
                observable10
                .subscribe(text -> value10.setText(text)));

        compositeDisposable.add(
                observable10
                .subscribe(text -> Toast.makeText(getApplicationContext(), "size 10: " + text, Toast.LENGTH_LONG).show()));

        compositeDisposable.add(
                observable20
                .subscribe(text -> value20.setText(text)));


        getContentResolver().insert(TextContentProvider.getRandomStringUri(10), new ContentValues());
        getContentResolver().insert(TextContentProvider.getRandomStringUri(20), new ContentValues());

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
