package derson.com.multipletheme.colorUi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences管理类
 */
public class SharedPreferencesMgr {

    private static SharedPreferences sPrefs;

    private SharedPreferencesMgr(Context context, String fileName) {
        sPrefs = context.getSharedPreferences(fileName, Context.MODE_WORLD_READABLE);
    }

    public static void init(Context context, String fileName) {
        new SharedPreferencesMgr(context, fileName);
    }

    public static int getInt(String key, int defaultValue) {
        return sPrefs.getInt(key, defaultValue);
    }

    public static void setInt(String key, int value) {
        sPrefs.edit().putInt(key, value).commit();
    }


}
