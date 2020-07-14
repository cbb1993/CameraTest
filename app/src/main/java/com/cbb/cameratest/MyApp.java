package com.cbb.cameratest;

import android.app.Application;

/**
 * Created by 坎坎.
 * Date: 2020/7/11
 * Time: 19:38
 * describe:
 */
public class MyApp extends Application {
    public static MyApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
