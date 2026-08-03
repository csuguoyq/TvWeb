package com.qiko.tvweb;

import android.app.Application;

public class QikoApp extends Application {
    public static QikoApp mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    public static QikoApp getInstance() {
        return mInstance;
    }
}
