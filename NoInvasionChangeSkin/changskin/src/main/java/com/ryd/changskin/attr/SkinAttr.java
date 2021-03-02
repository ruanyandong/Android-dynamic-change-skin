package com.ryd.changskin.attr;

import android.view.View;

/**
 *  皮肤属性，包含资源名称和属性类型
 */
public class SkinAttr {
    public String resName;
    public SkinAttrType attrType;

    public SkinAttr(SkinAttrType attrType, String resName) {
        this.resName = resName;
        this.attrType = attrType;
    }

    public void apply(View view) {
        attrType.apply(view, resName);
    }
}
