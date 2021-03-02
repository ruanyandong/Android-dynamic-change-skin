package com.ryd.changeskin;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import com.ryd.changeskin.utils.XLog;

public class ResourceManager {

    /**
     * 资源类型
     */
    private static final String DEFTYPE_DRAWABLE = "drawable";
    private static final String DEFTYPE_COLOR = "color";
    private final Resources mResources;
    /**
     * 插件包名
     */
    private final String mPluginPackageName;
    /**
     * 资源后缀
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
     * 获取drawable资源
     * @param name
     * @return
     */
    public Drawable getDrawableByName(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
        } catch (Resources.NotFoundException e) {
            try {
                return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));
            } catch (Resources.NotFoundException e2) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 获取color资源
     * @param name
     * @return
     */
    public int getColor(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getColor(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取colorSelect
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
     * 拼接资源名称后缀
     * @param name
     * @return
     */
    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix))
            return name + ("_" + mSuffix);
        return name;
    }

}
