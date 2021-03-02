package com.ryd.changskin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.ryd.changskin.utils.XLog;

public class ResourceManager {
    private static final String DEFTYPE_DRAWABLE = "drawable";
    private static final String DEFTYPE_COLOR = "color";
    private final Resources mResources;
    /**
     * 皮肤插件apk的包名
     */
    private final String mPluginPackageName;
    /**
     * 资源名称前缀
     */
    private final String mSuffix;


    public ResourceManager(Resources res, String pluginPackageName, String suffix) {
        mResources = res;
        mPluginPackageName = pluginPackageName;

        if (suffix == null) {
            suffix = "";
        }
        mSuffix = suffix;

    }

    /**
     * 根据资源名称获取drawable对象
     * @param name
     * @return
     */
    public Drawable getDrawableByName(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name + " , " + mPluginPackageName);
            return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据资源名称获取color资源
     * @param name
     * @return
     * @throws Resources.NotFoundException
     */
    public int getColor(String name) throws Resources.NotFoundException {
        name = appendSuffix(name);
        XLog.e("name = " + name);
        return mResources.getColor(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));
    }

    /**
     * 根据资源名称获取ColorStateList资源
     * @param name
     * @return
     */
    public ColorStateList getColorStateList(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getColorStateList(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     *  如果后缀有效，就用资源名称拼接后缀
     * @param name
     * @return
     */
    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix))
            return name + ("_" + mSuffix);
        return name;
    }

}
