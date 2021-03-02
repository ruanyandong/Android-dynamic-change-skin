package com.ryd.invasionchangeskin;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ryd.changeskin.SkinManager;
import com.ryd.changeskin.base.BaseSkinActivity;
import com.ryd.changeskin.callback.ISkinChangingCallback;
import com.ryd.changeskin.utils.XLog;

import java.io.File;
import java.lang.reflect.Method;

/**
 * http://blog.csdn.net/lmj623565791/article/details/41531475
 */
public class MainActivity extends BaseSkinActivity implements View.OnClickListener {

    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListView;

    private View mInnerChange01;
    private View mInnerChange02;

    private String mSkinPkgPath = Environment.getExternalStorageDirectory() + File.separator + "night_plugin.apk";
    private String[] mDatas = new String[]{"Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service"};


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestRuntimePermissions();
        initView();
        initEvents();

    }

    private void requestRuntimePermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            //第二步，如果没有授权，就授权
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.id_drawerLayout);

        mInnerChange01 = findViewById(R.id.id_rl_innerchange01);
        mInnerChange01.setOnClickListener(this);

        mInnerChange02 = findViewById(R.id.id_rl_innerchange02);
        mInnerChange02.setOnClickListener(this);

        findViewById(R.id.id_restore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().removeAnySkin();
            }
        });

        findViewById(R.id.id_changeskin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SkinManager.getInstance().changeSkin(mSkinPkgPath, "com.ryd.skinplugin", new ISkinChangingCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MainActivity.this, "换肤失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(MainActivity.this, "换肤成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_rl_innerchange01:
                SkinManager.getInstance().changeSkin("red");
                break;
            case R.id.id_rl_innerchange02:
                SkinManager.getInstance().changeSkin("green");
                break;
        }

    }

    private void initEvents() {

        mListView = (ListView) findViewById(R.id.id_listview);
        mListView.setAdapter(new ArrayAdapter<String>(this, -1, mDatas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent, false);
                }

                TextView tv = (TextView) convertView.findViewById(R.id.id_tv_title);
                tv.setText(getItem(position));
                return convertView;
            }
        });


//        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
////                View mContent = mDrawerLayout.getChildAt(0);
////                View mMenu = drawerView;
////                float scale = 1 - slideOffset;
////                float rightScale = 0.8f + scale * 0.2f;
////
////                if (drawerView.getTag().equals("LEFT")) {
////
////                    float leftScale = 1 - 0.3f * scale;
////
////                    ViewHelper.setScaleX(mMenu, leftScale);
////                    ViewHelper.setScaleY(mMenu, leftScale);
////                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
////                    ViewHelper.setTranslationX(mContent,
////                            mMenu.getMeasuredWidth() * (1 - scale));
////                    ViewHelper.setPivotX(mContent, 0);
////                    ViewHelper.setPivotY(mContent,
////                            mContent.getMeasuredHeight() / 2);
////                    mContent.invalidate();
////                    ViewHelper.setScaleX(mContent, rightScale);
////                    ViewHelper.setScaleY(mContent, rightScale);
////                }
//            }
//
//            @Override
//            public void onDrawerOpened(@NonNull View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerClosed(@NonNull View drawerView) {
//
//            }
//
//            @Override
//            public void onDrawerStateChanged(int newState) {
//
//            }
//        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.id_action_plugin_skinchange:
                SkinManager.getInstance().changeSkin(mSkinPkgPath, "com.ryd.skinplugin", new ISkinChangingCallback() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(MainActivity.this, "换肤失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        Toast.makeText(MainActivity.this, "换肤成功", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case R.id.id_action_remove_any_skin:
                SkinManager.getInstance().removeAnySkin();
                break;
            case R.id.id_action_test_res:
                AssetManager assetManager = null;
                try {
                    assetManager = AssetManager.class.newInstance();
                    Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
                    XLog.e("mSkinPkgPath "+mSkinPkgPath);
                    addAssetPath.invoke(assetManager, mSkinPkgPath);

                    File file = new File(mSkinPkgPath);
                    XLog.e("file.exists() "+file.exists() + "");
                    Resources superRes = getResources();
                    Resources mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());

                    int mainBgId = mResources.getIdentifier("skin_main_bg", "drawable", "com.ryd.skinplugin");
                    XLog.e("测试 mainBgId "+mainBgId);
                    findViewById(R.id.id_drawerLayout).setBackground(mResources.getDrawable(mainBgId));


                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
        }


        return super.onOptionsItemSelected(item);
    }

}
