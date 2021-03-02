package com.ryd.invasionchangeskin;

import android.app.Application;

import com.ryd.changeskin.SkinManager;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
    }
}
