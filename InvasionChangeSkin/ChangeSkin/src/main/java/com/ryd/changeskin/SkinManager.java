package com.ryd.changeskin;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.ryd.changeskin.attr.SkinView;
import com.ryd.changeskin.callback.ISkinChangedListener;
import com.ryd.changeskin.callback.ISkinChangingCallback;
import com.ryd.changeskin.utils.XLog;
import com.ryd.changeskin.utils.SpUtils;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkinManager {
    private Context mContext;
    private Resources mResources;
    private ResourceManager mResourceManager;
    private SpUtils mPrefUtils;

    /**
     * 是否使用皮肤插件或者说皮肤插件是否加载成功
     */
    private boolean usePlugin;
    /**
     * 换肤资源后缀
     */
    private String mSuffix = "";
    /**
     * 插件包的路径
     */
    private String mCurPluginPath;
    /**
     * 插件包包名
     */
    private String mCurPluginPkg;
    /**
     * 每个Activity实现一个ISkinChangedListener，对应此Activity内所有需要换肤的View  List<SkinView>
     */
    private final Map<ISkinChangedListener, List<SkinView>> mSkinViewMaps = new HashMap<ISkinChangedListener, List<SkinView>>();
    /**
     * 所有需要换肤的Activity
     */
    private final List<ISkinChangedListener> mSkinChangedListeners = new ArrayList<ISkinChangedListener>();

    private SkinManager() {
    }

    private static final class SingletonHolder {
        private final static SkinManager sInstance = new SkinManager();
    }

    public static SkinManager getInstance() {
        return SingletonHolder.sInstance;
    }

    /**
     * 从sp中取插件apk在外存中的路径和插件apk的包名
     *
     * @param context
     */
    public void init(Context context) {
        mContext = context.getApplicationContext();
        mPrefUtils = new SpUtils(mContext);

        String skinPluginPath = mPrefUtils.getPluginPath();
        String skinPluginPkg = mPrefUtils.getPluginPkgName();
        mSuffix = mPrefUtils.getSuffix();
        if (TextUtils.isEmpty(skinPluginPath)) {
            XLog.e("插件包为空");
            return;
        }
        File file = new File(skinPluginPath);
        if (!file.exists()) return;
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
     * 把插件包的皮肤资源加载到Resources对象中
     *
     * @param skinPath
     * @param skinPkgName
     * @param suffix
     * @throws Exception
     */
    private void loadPlugin(String skinPath, String skinPkgName, String suffix) throws Exception {
        //checkPluginParams(skinPath, skinPkgName);
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, skinPath);

        Resources superRes = mContext.getResources();
        mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mResourceManager = new ResourceManager(mResources, skinPkgName, suffix);
        usePlugin = true;
    }

    /**
     * 核对插件包的路径和包名是否为空
     * @param skinPath
     * @param skinPkgName
     * @return
     */
    private boolean checkPluginParams(String skinPath, String skinPkgName) {
        if (TextUtils.isEmpty(skinPath) || TextUtils.isEmpty(skinPkgName)) {
            return false;
        }
        return true;
    }

    /**
     * 如果插件包的路径和包名为空就抛出异常终止程序执行
     * @param skinPath
     * @param skinPkgName
     */
    private void checkPluginParamsThrow(String skinPath, String skinPkgName) {
        if (!checkPluginParams(skinPath, skinPkgName)) {
            throw new IllegalArgumentException("skinPluginPath or skinPkgName can not be empty ! ");
        }
    }

    /**
     * 清除皮肤插件包信息，还原皮肤
     */
    public void removeAnySkin() {
        clearPluginInfo();
        notifyChangedListeners();
    }

    /**
     * 如果插件加载成功或者皮肤资源后缀不为空就需要换肤
     * @return
     */
    public boolean needChangeSkin() {
        return usePlugin || !TextUtils.isEmpty(mSuffix);
    }


    /**
     * 如果插件没有加载成功，就创建ResourceManager
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


    public void changeSkin(final String skinPluginPath, final String pkgName, ISkinChangingCallback callback) {
        changeSkin(skinPluginPath, pkgName, "", callback);
    }


    /**
     * 根据suffix选择插件内某套皮肤，默认为""
     *
     * @param skinPluginPath
     * @param pkgName
     * @param suffix
     * @param callback
     */
    public void changeSkin(final String skinPluginPath, final String pkgName, final String suffix, ISkinChangingCallback callback) {
        if (callback == null)
            callback = ISkinChangingCallback.DEFAULT_SKIN_CHANGING_CALLBACK;
        final ISkinChangingCallback skinChangingCallback = callback;

        // 回调开始换肤
        skinChangingCallback.onStart();
        // 如果插件路径和包名为空就抛异常
        checkPluginParamsThrow(skinPluginPath, pkgName);
        // 如果新的皮肤插件和当前的皮肤插件相同则返回
        if (skinPluginPath.equals(mCurPluginPath) && pkgName.equals(mCurPluginPkg)) {
            return;
        }

        new AsyncTask<Void, Void, Void>() {
            /**
             * 运行在线程池中
             * @param params
             * @return
             */
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    // 加载插件
                    loadPlugin(skinPluginPath, pkgName, suffix);
                } catch (Exception e) {
                    e.printStackTrace();
                    skinChangingCallback.onError(e);
                }

                return null;
            }

            /**
             * 运行在UI线程
             * @param aVoid
             */
            @Override
            protected void onPostExecute(Void aVoid) {
                try {
                    // 更新插件信息
                    updatePluginInfo(skinPluginPath, pkgName, suffix);
                    // 开始换肤
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
     * 把每个activity和需要换肤的view存放在map中
     *
     * @param listener
     * @param skinViews
     */
    public void addSkinView(ISkinChangedListener listener, List<SkinView> skinViews) {
        mSkinViewMaps.put(listener, skinViews);
    }

    /**
     * 取出activity中所有需要换肤的view
     * @param listener
     * @return
     */
    public List<SkinView> getSkinViews(ISkinChangedListener listener) {
        return mSkinViewMaps.get(listener);
    }

    /**
     * 对每个进行换肤操作
     * @param listener
     */
    public void apply(ISkinChangedListener listener) {
        List<SkinView> skinViews = getSkinViews(listener);

        if (skinViews == null) return;
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    /**
     * 收集要换肤的Activity
     * @param listener
     */
    public void addChangedListener(ISkinChangedListener listener) {
        mSkinChangedListeners.add(listener);
    }

    /**
     * 移除要换肤的Activity
     * @param listener
     */
    public void removeChangedListener(ISkinChangedListener listener) {
        mSkinChangedListeners.remove(listener);
        mSkinViewMaps.remove(listener);
    }

    /**
     * 对所有需要换肤的Activity进行换肤操作
     */
    public void notifyChangedListeners() {
        for (ISkinChangedListener listener : mSkinChangedListeners) {
            listener.onSkinChanged();
        }
    }

}
