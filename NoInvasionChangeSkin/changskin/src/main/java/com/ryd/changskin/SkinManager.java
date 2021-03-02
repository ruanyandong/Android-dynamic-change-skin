package com.ryd.changskin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import com.ryd.changskin.attr.SkinAttrSupport;
import com.ryd.changskin.attr.SkinView;
import com.ryd.changskin.callback.ISkinChangingCallback;
import com.ryd.changskin.utils.PrefUtils;
import com.ryd.changskin.utils.XLog;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SkinManager {

    private Context mContext;
    private Resources mResources;
    private ResourceManager mResourceManager;
    private PrefUtils mPrefUtils;

    /**
     * 是否使用插件
     */
    private boolean usePlugin;
    /**
     *  资源后缀
     */
    private String mSuffix = "";
    /**
     * 当前使用的插件路径
     */
    private String mCurPluginPath;
    /**
     * 当前使用的插件包名称
     */
    private String mCurPluginPkg;

    /**
     * 所有需要换肤
     */
    private final List<Activity> mActivities = new ArrayList<Activity>();

    private SkinManager() {
    }

    private static class SingletonHolder {
        static SkinManager sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 从sp读取插件路径和插件包名以及资源后缀，如果插件路径和插件包名有效则加载插件
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new PrefUtils(mContext);

        String skinPluginPath = mPrefUtils.getPluginPath();
        String skinPluginPkg = mPrefUtils.getPluginPkgName();
        mSuffix = mPrefUtils.getSuffix();

        if (!validPluginParams(skinPluginPath, skinPluginPkg))
            return;

        try {
            loadPlugin(skinPluginPath, skinPluginPkg, mSuffix);
            mCurPluginPath = skinPluginPath;
            mCurPluginPkg = skinPluginPkg;
        } catch (Exception e) {
            mPrefUtils.clear();
            e.printStackTrace();
        }
    }

    /**
     * 获取插件包信息
     * @param skinPluginPath
     * @return
     */
    private PackageInfo getPackageInfo(String skinPluginPath) {
        PackageManager pm = mContext.getPackageManager();
        return pm.getPackageArchiveInfo(skinPluginPath, PackageManager.GET_ACTIVITIES);
    }

    /**
     * 加载插件apk内的换肤资源
     * @param skinPath
     * @param skinPkgName
     * @param suffix
     * @throws Exception
     */
    private void loadPlugin(String skinPath, String skinPkgName, String suffix) throws Exception {
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, skinPath);

        Resources superRes = mContext.getResources();
        mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mResourceManager = new ResourceManager(mResources, skinPkgName, suffix);
        usePlugin = true;
    }

    /**
     *  验证插件路径和插件包名是否有效
     * @param skinPath
     * @param skinPkgName
     * @return
     */
    private boolean validPluginParams(String skinPath, String skinPkgName) {
        if (TextUtils.isEmpty(skinPath) || TextUtils.isEmpty(skinPkgName)) {
            return false;
        }

        File file = new File(skinPath);
        if (!file.exists())
            return false;

        PackageInfo info = getPackageInfo(skinPath);
        if (!info.packageName.equals(skinPkgName))
            return false;
        return true;
    }

    /**
     * 如果插件路径和包名无效则抛出异常
     * @param skinPath
     * @param skinPkgName
     */
    private void checkPluginParamsThrow(String skinPath, String skinPkgName) {
        if (!validPluginParams(skinPath, skinPkgName)) {
            throw new IllegalArgumentException("skinPluginPath or skinPkgName not valid ! ");
        }
    }

    /**
     * 移除所有换肤
     */
    public void removeAnySkin() {
        XLog.e("removeAnySkin");
        clearPluginInfo();
        notifyChangedListeners();
    }

    /**
     * 如果使用插件或者后缀不为空则需要换肤
     * @return
     */
    public boolean needChangeSkin() {
        return usePlugin || !TextUtils.isEmpty(mSuffix);
    }

    /**
     * 获取资源管理器对象
     * @return
     */
    public ResourceManager getResourceManager() {
        if (!usePlugin) {
            mResourceManager = new ResourceManager(mContext.getResources(), mContext.getPackageName(), mSuffix);
        }
        return mResourceManager;
    }


    /**
     * 应用内换肤，传入资源区别的后缀
     *
     * @param suffix
     */
    public void changeSkin(String suffix) {
        clearPluginInfo();//clear before
        mSuffix = suffix;
        mPrefUtils.putPluginSuffix(suffix);
        notifyChangedListeners();
    }

    /**
     * 清理插件信息
     */
    private void clearPluginInfo() {
        mCurPluginPath = null;
        mCurPluginPkg = null;
        usePlugin = false;
        mSuffix = null;
        mPrefUtils.clear();
    }

    /**
     * 更新插件信息
     * @param skinPluginPath
     * @param pkgName
     * @param suffix
     */
    private void updatePluginInfo(String skinPluginPath, String pkgName, String suffix) {
        mPrefUtils.putPluginPath(skinPluginPath);
        mPrefUtils.putPluginPkg(pkgName);
        mPrefUtils.putPluginSuffix(suffix);

        mCurPluginPkg = pkgName;
        mCurPluginPath = skinPluginPath;
        mSuffix = suffix;
    }

    /**
     * 加载没有后缀的皮肤
     * @param skinPluginPath
     * @param skinPluginPkg
     * @param callback
     */
    public void changeSkin(final String skinPluginPath, final String skinPluginPkg, ISkinChangingCallback callback) {
        changeSkin(skinPluginPath, skinPluginPkg, null, callback);
    }


    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     *
     * @param skinPluginPath
     * @param skinPluginPkg
     * @param suffix
     * @param callback
     */
    public void changeSkin(final String skinPluginPath, final String skinPluginPkg, final String suffix, ISkinChangingCallback callback) {
        XLog.e("changeSkin = " + skinPluginPath + " , " + skinPluginPkg);
        if (callback == null)
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        final ISkinChangingCallback skinChangingCallback = callback;

        skinChangingCallback.onStart();

        try {
            checkPluginParamsThrow(skinPluginPath, skinPluginPkg);
        } catch (IllegalArgumentException e) {
            skinChangingCallback.onError(new RuntimeException("checkPlugin occur error"));
            return;
        }

        new AsyncTask<Void, Void, Integer>() {
            /**
             * 在线程池中加载插件
             * @param params
             * @return
             */
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    loadPlugin(skinPluginPath, skinPluginPkg, suffix);
                    return 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }

            }

            /**
             * 在UI线程换肤
             * @param res
             */
            @Override
            protected void onPostExecute(Integer res) {
                if (res == 0) {
                    skinChangingCallback.onError(new RuntimeException("loadPlugin occur error"));
                    return;
                }
                try {
                    updatePluginInfo(skinPluginPath, skinPluginPkg, suffix);
                    notifyChangedListeners();
                    skinChangingCallback.onComplete();
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

            }
        }.execute();
    }

    /**
     * 对当前Activity进行换肤
     * @param activity
     */
    public void apply(Activity activity) {
        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(activity);
        if (skinViews == null) return;
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    /**
     * 对当前Activity进行换肤
     * @param activity
     */
    public void register(final Activity activity) {
        mActivities.add(activity);

        activity.findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                apply(activity);
            }
        });
    }

    /**
     * 移除当前换肤的Activity
     * @param activity
     */
    public void unregister(Activity activity) {
        mActivities.remove(activity);
    }

    /**
     * 对所有注册了的Activity进行焕肤
     */
    public void notifyChangedListeners() {

        for (Activity activity : mActivities) {
            apply(activity);
        }
    }

    /**
     * 对当前View和其子View进行焕肤
     *
     * @param view
     */
    public void injectSkin(View view) {
        List<SkinView> skinViews = new ArrayList<SkinView>();
        SkinAttrSupport.addSkinViews(view, skinViews);
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }


}
