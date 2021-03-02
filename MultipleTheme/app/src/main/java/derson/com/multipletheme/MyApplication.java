package derson.com.multipletheme;

import android.app.Application;
import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferencesMgr.init(this, "derson");
    }
}
