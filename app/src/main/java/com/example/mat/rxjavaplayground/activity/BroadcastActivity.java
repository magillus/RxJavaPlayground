package com.example.mat.rxjavaplayground.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxutil.RxBroadcastReceiver;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 12/22/16.
 */

public class BroadcastActivity extends AppCompatActivity {

    public static final String ACTION_TEST_A_BROADCAST = "com.example.mat.rxjavaplayground.ACTION_TEST_A_BROADCAST";
    public static final String ACTION_TEST_B_BROADCAST = "com.example.mat.rxjavaplayground.ACTION_TEST_B_BROADCAST";
    public static final String PARAM_TEST_STRING = "TEST_STRING";
    private Unbinder unbinder;

    CompositeDisposable compositeDisposable = new CompositeDisposable();
    int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast);
        unbinder = ButterKnife.bind(this);

        // creates single instance of observable - we got the SHARE()
        if (broadcastObservable == null) {
            broadcastObservable = RxBroadcastReceiver.create(this, getIntentFilter()).share();
        }
    }

    Observable<Intent> broadcastObservable;

    @Override
    protected void onResume() {
        super.onResume();

        compositeDisposable.add(broadcastObservable
                .map(this::intentParseA) // we love lambda functions
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show())
        );

        compositeDisposable.add(broadcastObservable
                .map(BroadcastActivity::intentParseB) // we love static lambdas
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> Toast.makeText(this, text, Toast.LENGTH_SHORT).show())
        );
    }

    private static String intentParseB(Intent intent) {
        return intent.getStringExtra(PARAM_TEST_STRING);
    }

    private String intentParseA(Intent intent) {
        return intent.getStringExtra(PARAM_TEST_STRING);
    }

    @Override
    protected void onPause() {
        super.onPause();
        compositeDisposable.clear();
    }

    @OnClick(R.id.btn_send_broadcast_a)
    public void onSendBroadcastA(View v) {
        Intent intent = new Intent(ACTION_TEST_A_BROADCAST);
        intent.setType("text/plain");
        intent.putExtra(PARAM_TEST_STRING, String.format("Test A broadcast #%d", count++));
        sendBroadcast(intent);
    }

    @OnClick(R.id.btn_send_broadcast_b)
    public void onSendBroadcastB(View v) {
        Intent intent = new Intent(ACTION_TEST_B_BROADCAST);
        intent.setType("text/plain");
        intent.putExtra(PARAM_TEST_STRING, String.format("Test B broadcast #%d", count++));
        sendBroadcast(intent);
    }


    private IntentFilter getIntentFilter() {
        IntentFilter intentFilter = IntentFilter.create(ACTION_TEST_A_BROADCAST, "text/plain");
        intentFilter.addAction(ACTION_TEST_B_BROADCAST);
        return intentFilter;
    }


}
