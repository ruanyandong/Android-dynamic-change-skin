package com.ryd.noinvasionchangeskin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.ryd.changskin.SkinManager;
import com.ryd.changskin.callback.ISkinChangingCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DrawerLayout mDrawerLayout;
    private ListView mListView;

    private Toolbar mToolbar;

    private View mInnerChange01;
    private View mInnerChange02;

    private String mSkinPkgPath = Environment.getExternalStorageDirectory() + File.separator + "skin_plugin.apk";

    private List<String> mDatas = new ArrayList<String>(Arrays.asList("Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service", "Activity", "Service", "Activity", "Service",
            "Activity", "Service", "Activity", "Service"));

    private ArrayAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SkinManager.getInstance().register(this);

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


    private void initEvents() {

        mListView = (ListView) findViewById(R.id.id_listview);
        mListView.setAdapter(mAdapter = new ArrayAdapter<String>(this, -1, mDatas) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item, parent
                            , false);

                }
                SkinManager.getInstance().injectSkin(convertView);
                TextView tv = (TextView) convertView.findViewById(R.id.id_tv_title);
                tv.setText(getItem(position));
                return convertView;
            }
        });


//        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
//            @Override
//            public void onDrawerStateChanged(int newState) {
//            }
//
//            @Override
//            public void onDrawerSlide(View drawerView, float slideOffset) {
//                View mContent = mDrawerLayout.getChildAt(0);
//                View mMenu = drawerView;
//                float scale = 1 - slideOffset;
//                float rightScale = 0.8f + scale * 0.2f;
//
//                if (drawerView.getTag().equals("LEFT")) {
//
//                    float leftScale = 1 - 0.3f * scale;
//
//                    ViewHelper.setScaleX(mMenu, leftScale);
//                    ViewHelper.setScaleY(mMenu, leftScale);
//                    ViewHelper.setAlpha(mMenu, 0.6f + 0.4f * (1 - scale));
//                    ViewHelper.setTranslationX(mContent,
//                            mMenu.getMeasuredWidth() * (1 - scale));
//                    ViewHelper.setPivotX(mContent, 0);
//                    ViewHelper.setPivotY(mContent,
//                            mContent.getMeasuredHeight() / 2);
//                    mContent.invalidate();
//                    ViewHelper.setScaleX(mContent, rightScale);
//                    ViewHelper.setScaleY(mContent, rightScale);
//                }
//            }
//
//            @Override
//            public void onDrawerOpened(View drawerView) {
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
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
                        Toast.makeText(MainActivity.this, "换肤失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            case R.id.id_action_notify_lv:

                for (int i = 0, n = mDatas.size(); i < n; i++) {
                    mDatas.set(i, mDatas.get(i) + " changed");
                }
                mAdapter.notifyDataSetChanged();
                break;
            case R.id.id_action_dynamic:
                Intent intent = new Intent(this, TestTagActivity.class);
                startActivity(intent);
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().unregister(this);
    }
}
