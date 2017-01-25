package com.wajumbie.robot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    MockMainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MockMainPresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.startDetectingHumans();

    }


    @Override
    protected void onStop() {
        super.onStop();
    }
}
