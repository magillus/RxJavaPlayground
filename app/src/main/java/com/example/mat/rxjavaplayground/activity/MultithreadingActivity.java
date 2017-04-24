package com.example.mat.rxjavaplayground.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.service.DataService;
import com.example.mat.rxjavaplayground.service.StringConverters;
import com.example.mat.rxjavaplayground.utils.ThreadLogging;
import com.jakewharton.rxrelay2.PublishRelay;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class MultithreadingActivity extends BaseChildActivity {

    CompositeDisposable compositeDisposable;
    Disposable fetchDataDDisposable;
    DataService dataService;

    Unbinder unbinder;
    @BindView(R.id.val_A)
    TextView valAView;
    @BindView(R.id.val_B)
    TextView valBView;
    @BindView(R.id.val_C)
    TextView valCView;
    @BindView(R.id.val_D)
    TextView valDView;
    @BindView(R.id.val_E)
    TextView valEView;
    @BindView(R.id.val_F)
    TextView valFView;


    private Observable<String> testObservableBC;

    private Observable<String> testObservableE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multithreading);
        unbinder = ButterKnife.bind(this);
        dataService = new DataService();
    }

    @Override
    protected void onResume() {
        super.onResume();
        compositeDisposable = new CompositeDisposable();
        compositeDisposable.add(fetchDataA());
        compositeDisposable.add(fetchDataB());
        compositeDisposable.add(fetchDataC());
        compositeDisposable.add(fetchDataE());
        compositeDisposable.add(fetchDataF());
        fetchDataDDisposable = fetchDataD();
    }

    private Observable<String> getTestObservableBC() {
        if (testObservableBC == null) {
            testObservableBC = dataService.getStringRepeated("test BC", 16, 5000)
                    .map(text -> "text:" + text)
                    .replay(1).autoConnect();
        }
        return testObservableBC;
    }

    private Observable<String> getTestObservableEF() {
        if (testObservableE == null) {
            testObservableE = dataService.getStringReapatedDefer("test E", 25, 4000)
                    .replay(1).autoConnect();
        }
        return testObservableE;
    }

    private Disposable fetchDataA() {
        return dataService.getStringReapatedDefer("test A", 10, 10 * 1000)
                .map(StringConverters::toUppercase)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> valAView.setText(text), Timber::e);
    }

    private Disposable fetchDataB() {
        return getTestObservableBC()
                .observeOn(AndroidSchedulers.mainThread()) // remove for error
                .subscribe(count -> valBView.setText(String.valueOf(count)), Timber::e);
    }

    private Disposable fetchDataC() {
        return getTestObservableBC()
                .map(StringConverters::toUppercase)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> valCView.setText(String.valueOf(count)), Timber::e);
    }

    private Disposable fetchDataD() {
        return dataService.getStringRepeated("test D", 20, 15 * 1000)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> valDView.setText(text));
    }

    private Disposable fetchDataE() {
        return getTestObservableEF()
                .map(StringConverters::toLowercase)
                .map(text -> {
                    int aIndex = text.indexOf("a");
                    if (aIndex < 0) {
                        return "a - not found";
                    }
                    if (aIndex + 1 % 2 == 0) {
                        throw new Exception("we got 'a' in even position index");
                    }
                    return text;
                })
                /// error will happen
                // however we do not want to stop emitting values
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturnItem("got error")
                .subscribe(valEView::setText, Timber::e);
    }

    private Disposable fetchDataF() {
        return getTestObservableEF()
                .map(textToConvert -> StringConverters.countLetters(textToConvert, "abc"))
                .map(countLetters -> "# of 'abc' characters:" + String.valueOf(countLetters))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(valFView::setText);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
        if (!fetchDataDDisposable.isDisposed()) {
            fetchDataDDisposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
