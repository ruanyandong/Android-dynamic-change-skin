package com.ryd.changeskin.attr;

import android.content.Context;
import android.util.AttributeSet;

import com.ryd.changeskin.constant.SkinConfig;
import com.ryd.changeskin.utils.XLog;

import java.util.ArrayList;
import java.util.List;

public class SkinAttrSupport {

    public static List<SkinAttr> getSkinAttrs(AttributeSet attrs, Context context) {
        List<SkinAttr> skinAttrs = new ArrayList<SkinAttr>();
        SkinAttr skinAttr = null;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            // 属性名，例如src，layout_width等等
            String attrName = attrs.getAttributeName(i);
            // 对应属性的值
            String attrValue = attrs.getAttributeValue(i);
            XLog.e("attrName "+attrName+" attrValue "+attrValue);
            // 判断换肤所支持的属性
            SkinAttrType attrType = getSupprotAttrType(attrName);
            if (attrType == null) continue;

            // 引用资源的attrValue都是@开头的，例如textColor attrValue @2131034312
            if (attrValue.startsWith("@")) {
                int id = Integer.parseInt(attrValue.substring(1));
                String entryName = context.getResources().getResourceEntryName(id);
                //entryName是资源的名称 如skin_item_text_color
                XLog.e("entryName = " + entryName);
                if (entryName.startsWith(SkinConfig.ATTR_PREFIX)) {
                    skinAttr = new SkinAttr(attrType, entryName);
                    skinAttrs.add(skinAttr);
                }
            }
        }
        return skinAttrs;

    }

    /**
     * 如果是支持的皮肤属性就返回
     * @param attrName
     * @return
     */
    private static SkinAttrType getSupprotAttrType(String attrName) {
        for (SkinAttrType attrType : SkinAttrType.values()) {
            if (attrType.getAttrType().equals(attrName))
                return attrType;
        }
        return null;
    }

}
