package com.ryd.changeskin.attr;

import android.view.View;

/**
 * 每个View对应的换肤属性
 */
public class SkinAttr {
    String resName;
    SkinAttrType attrType;


    public SkinAttr(SkinAttrType attrType, String resName) {
        this.resName = resName;
        this.attrType = attrType;
    }

    public void apply(View view) {
        attrType.apply(view, resName);
    }
}
