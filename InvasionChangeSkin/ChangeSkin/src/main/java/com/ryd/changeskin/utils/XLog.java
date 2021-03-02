package com.ryd.changeskin.utils;

import android.util.Log;

public class XLog {
    private static final String TAG = "ruanyandong";
    private static boolean debug = true;

    public static void e(String msg) {
        if (debug)
            Log.e(TAG, msg);
    }
}
