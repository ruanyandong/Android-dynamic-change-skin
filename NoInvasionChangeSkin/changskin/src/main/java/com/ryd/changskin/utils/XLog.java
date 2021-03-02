package com.ryd.changskin.utils;

import android.util.Log;


public class XLog {
    private static final String TAG = "ruanyandong";
    private static final boolean debug = true;

    public static void e(String msg) {
        if (debug)
            Log.e(TAG, msg);
    }

}
