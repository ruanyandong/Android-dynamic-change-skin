package derson.com.multipletheme;

import android.app.Activity;
import android.os.Bundle;
import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;

public class BaseActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTheme方法要在setContentView之前调用
        if(SharedPreferencesMgr.getInt("theme", 0) == 1) {
            setTheme(R.style.theme_2);
        } else {
            setTheme(R.style.theme_1);
        }
    }
}
