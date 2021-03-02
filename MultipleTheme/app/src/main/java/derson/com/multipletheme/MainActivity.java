package derson.com.multipletheme;

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import derson.com.multipletheme.colorUi.util.ColorUiUtil;
import derson.com.multipletheme.colorUi.util.SharedPreferencesMgr;
import derson.com.multipletheme.colorUi.widget.ColorButton;

/**
 * 使用相同的资源id，但在不同的Theme下边自定义不同的资源。
 * 我们通过主动切换到不同的Theme从而切换界面元素创建时使用的资源。
 * 这种方案的代码量不多，而且有个很明显的缺点不支持已经创建界面的换肤，必须重新加载界面元素。
 */

public class MainActivity extends BaseActivity {

    ColorButton btn, btn_next;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = findViewById(R.id.btn);
        btn.setBackgroundResource(R.attr.main_btn_bg);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SharedPreferencesMgr.getInt("theme", 0) == 1) {
                    SharedPreferencesMgr.setInt("theme", 0);
                    setTheme(R.style.theme_1);
                } else {
                    SharedPreferencesMgr.setInt("theme", 1);
                    setTheme(R.style.theme_2);
                }
                final View rootView = getWindow().getDecorView();
//                if(Build.VERSION.SDK_INT < 14) {
//                    ColorUiUtil.changeTheme(rootView, getTheme());
//
//                } else {
                rootView.setDrawingCacheEnabled(true);
                rootView.buildDrawingCache(true);
                final Bitmap localBitmap = Bitmap.createBitmap(rootView.getDrawingCache());
                rootView.setDrawingCacheEnabled(false);
                if (null != localBitmap && rootView instanceof ViewGroup) {
                    final View localView2 = new View(getApplicationContext());
                    localView2.setBackgroundDrawable(new BitmapDrawable(getResources(), localBitmap));
                    ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    ((ViewGroup) rootView).addView(localView2, params);
                    localView2.animate().alpha(0).setDuration(400).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            ColorUiUtil.changeTheme(rootView, getTheme());
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ((ViewGroup) rootView).removeView(localView2);
                            localBitmap.recycle();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).start();
                }
            }
//            }
        });
        btn_next = (ColorButton) findViewById(R.id.btn_2);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
    }

}
