package com.example.mat.rxjavaplayground.activity;

import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.TextView;

import com.example.mat.rxjavaplayground.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created on 1/6/17.
 */

public class NetworkTestActivity extends BaseChildActivity {

    private Unbinder unbinder;
    @BindView(R.id.txt_loaded_text)
    TextView loadedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_sample);
        unbinder = ButterKnife.bind(this);

    }

    @OnClick(R.id.btn_single_call)
    public void onSingleCall(View v) {
        loadedText.setText("loading...");
        loadFromNetwork()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    if (!isDestroyed()) {
                        loadedText.setText(text);
                    }
                });
    }

    private Observable<String> loadFromCache() {
        return Observable.create(e -> e.onNext(localValue));
    }

    String localValue = "Text Value from cache.";

    private Single<String> loadFromNetwork() {
        return Single.fromCallable(() -> {
            SystemClock.sleep(2000);// delay for network call time
            URL url = new URL("http://www.contextmediainc.com");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer sb = new StringBuffer();

            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
            return sb.toString();
        });
    }

    @OnClick(R.id.btn_call_cache)
    public void onCallWithCache(View v) {
        Observable.merge(
                loadFromCache().subscribeOn(Schedulers.io()),
                loadFromNetwork().toObservable().map(text -> this.localValue = text))
                .subscribeOn(Schedulers.io())

                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> {
                    if (!isDestroyed()) {
                        loadedText.setText(text);
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unbinder.unbind();
    }
}
