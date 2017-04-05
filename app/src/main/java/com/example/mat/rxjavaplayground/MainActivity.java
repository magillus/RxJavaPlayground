package com.example.mat.rxjavaplayground;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.mat.rxjavaplayground.fragment.SelectorFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, new SelectorFragment())
                .commit();
    }
}
