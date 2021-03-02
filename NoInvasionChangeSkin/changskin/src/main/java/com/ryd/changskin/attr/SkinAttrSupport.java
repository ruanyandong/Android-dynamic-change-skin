package com.ryd.changskin.attr;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ryd.changskin.R;
import com.ryd.changskin.constant.SkinConfig;
import com.ryd.changskin.utils.XLog;

import java.util.ArrayList;
import java.util.List;

public class SkinAttrSupport {

    /**
     * 判断是否支持该属性换肤
     *
     * @param attrName
     * @return
     */
    private static SkinAttrType getSupportAttrType(String attrName) {
        for (SkinAttrType attrType : SkinAttrType.values()) {
            if (attrType.getAttrType().equals(attrName))
                return attrType;
        }
        return null;
    }

    /**
     * 传入activity，找到content元素，递归遍历所有的子View，根据tag命名，记录需要换肤的View
     *
     * @param activity
     */
    public static List<SkinView> getSkinViews(Activity activity) {
        List<SkinView> skinViews = new ArrayList<SkinView>();
        ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
        addSkinViews(content, skinViews);
        return skinViews;
    }

    /**
     *  根据当前Activity的id为android.R.id.content的控件，递归的遍历所有View，获取对应的换肤属性
     * @param view
     * @param skinViews
     */
    public static void addSkinViews(View view, List<SkinView> skinViews) {
        SkinView skinView = getSkinView(view);
        if (skinView != null) skinViews.add(skinView);

        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;

            for (int i = 0, n = container.getChildCount(); i < n; i++) {
                View child = container.getChildAt(i);
                addSkinViews(child, skinViews);
            }
        }

    }

    /**
     * 根据当前View的tag获取换肤属性和对应的资源名称，然后根据View和换肤属性列表构造SkinView
     * @param view
     * @return
     */
    public static SkinView getSkinView(View view) {
        Object tag = view.getTag(R.id.skin_tag_id);
        if (tag == null) {
            tag = view.getTag();
        }
        if (tag == null) return null;
        if (!(tag instanceof String)) return null;
        String tagStr = (String) tag;

        List<SkinAttr> skinAttrs = parseTag(tagStr);
        if (!skinAttrs.isEmpty()) {
            changeViewTag(view);
            return new SkinView(view, skinAttrs);
        }
        return null;
    }

    /**
     * 给当前View设置tag
     * @param view
     */
    private static void changeViewTag(View view) {
        Object tag = view.getTag(R.id.skin_tag_id);
        if (tag == null) {
            tag = view.getTag();
            XLog.e("view.getTag() == "+tag);
            view.setTag(R.id.skin_tag_id, tag);
            view.setTag(null);
        }
    }

    /**
     * 根据tag进行解析，获取所有支持换肤的属性放在list中，每个皮肤属性包含 view的属性类型 和 资源名称
     * @param tagStr
     * @return
     */
    //skin:left_menu_icon:src|skin:color_red:textColor
    private static List<SkinAttr> parseTag(String tagStr) {
        List<SkinAttr> skinAttrs = new ArrayList<SkinAttr>();
        if (TextUtils.isEmpty(tagStr)) return skinAttrs;

        String[] items = tagStr.split("[|]");
        for (String item : items) {
            if (!item.startsWith(SkinConfig.SKIN_PREFIX))
                continue;
            String[] resItems = item.split(":");
            if (resItems.length != 3)
                continue;

            String resName = resItems[1];
            String resType = resItems[2];

            SkinAttrType attrType = getSupportAttrType(resType);
            if (attrType == null) continue;
            SkinAttr attr = new SkinAttr(attrType, resName);
            skinAttrs.add(attr);
        }
        return skinAttrs;
    }

}
