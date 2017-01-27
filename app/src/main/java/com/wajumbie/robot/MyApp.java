package com.wajumbie.robot;

import android.app.Application;

import butterknife.ButterKnife;

/**
 * Created by Tyler on 1/23/2017.
 */

public class MyApp extends Application {
    static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        ButterKnife.setDebug(true);
    }


    public static MyApp getShared() {
        return instance;
    }
}
