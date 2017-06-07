package com.example.mat.rxjavaplayground.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.rxutil.RealmContext;

import io.realm.RealmConfiguration;

/**
 * Created by mateusz.perlak on 5/19/17.
 */

public class RealmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realm_activity);

        RealmContext realmContext = new RealmContext(this,
                new RealmConfiguration.Builder()
                        .name("TEST")
                        .deleteRealmIfMigrationNeeded()
                        .schemaVersion(1)
                        .build());


    }
}
