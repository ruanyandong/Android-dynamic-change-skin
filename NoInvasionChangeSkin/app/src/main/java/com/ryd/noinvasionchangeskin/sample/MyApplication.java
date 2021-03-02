package com.ryd.noinvasionchangeskin.sample;

import android.app.Application;
import com.ryd.changskin.SkinManager;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SkinManager.getInstance().init(this);
    }
}
