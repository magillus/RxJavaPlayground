package com.example.mat.rxjavaplayground.activity;

import android.os.Bundle;
import android.os.SystemClock;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.db.DataEntry;
import com.example.mat.rxjavaplayground.db.DataSqliteHelper;
import com.example.mat.rxjavaplayground.service.DataService;
import com.example.mat.rxjavaplayground.service.RandomString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created on 1/17/17.
 */

public class DatabaseActivity extends BaseChildActivity {

    Unbinder unbinder;

    DataSqliteHelper dataSqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);
        unbinder = ButterKnife.bind(this);

        dataSqliteHelper = new DataSqliteHelper(this);
        prepareData();

    }

    private void prepareData() {
        dataSqliteHelper.onUpgrade(dataSqliteHelper.getWritableDatabase(), 0, 1);
        RandomString randomString = new RandomString(30);
        List<DataEntry> dataEntryList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataEntryList.add(new DataEntry(randomString.nextString().substring(0, 10), randomString.nextString(), new Date(System.currentTimeMillis())));
        }
        dataSqliteHelper.insertData(dataEntryList);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        dataSqliteHelper.close();
    }
}
