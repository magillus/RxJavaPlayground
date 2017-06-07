package com.example.mat.rxjavaplayground.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mat.rxjavaplayground.R;
import com.example.mat.rxjavaplayground.activity.BroadcastActivity;
import com.example.mat.rxjavaplayground.activity.ContentProviderActivity;
import com.example.mat.rxjavaplayground.activity.DatabaseActivity;
import com.example.mat.rxjavaplayground.activity.MultithreadingActivity;
import com.example.mat.rxjavaplayground.activity.NetworkTestActivity;
import com.example.mat.rxjavaplayground.activity.RealmActivity;
import com.example.mat.rxjavaplayground.activity.RxServiceActivity;
import com.example.mat.rxjavaplayground.activity.SinglesObservableActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectorFragment extends Fragment {

    private Unbinder unbinder;

    public SelectorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selector, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_multi_threading)
    public void onMultiThreading(View v) {
        startActivity(new Intent(getContext(), MultithreadingActivity.class));
    }

    /**
     * Network call example
     *
     * @param v
     */
    @OnClick(R.id.btn_network)
    public void onNetwork(View v) {
        startActivity(new Intent(getContext(), NetworkTestActivity.class));
    }

    @OnClick(R.id.btn_service_wrapper)
    public void onRxService(View v) {
        startActivity(new Intent(getContext(), RxServiceActivity.class));
    }

    /**
     * Loading data from persistent storage
     *
     * @param v
     */
    @OnClick(R.id.btn_data_fetch)
    public void onDataFetch(View v) {
        startActivity(new Intent(getContext(), DatabaseActivity.class));
    }

    /**
     * Storing data off the ui thread and getting notification
     *
     * @param v
     */
    @OnClick(R.id.btn_data_update)
    public void onDataUpdate(View v) {

    }

    @OnClick(R.id.btn_content_provider)
    public void onContentProvider(View v) {
        startActivity(new Intent(getContext(), ContentProviderActivity.class));
    }

    @OnClick(R.id.btn_broadcast_receier)
    public void onBroadcastReceiver(View v) {
        startActivity(new Intent(getContext(), BroadcastActivity.class));
    }

    @OnClick(R.id.btn_single_tests)
    public void onSingleDisposableTest(View v) {
        startActivity(new Intent(getContext(), SinglesObservableActivity.class));
    }

    @OnClick(R.id.btn_realm_tests)
    public void onRealmTest(View v) {
        startActivity(new Intent(getContext(), RealmActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
