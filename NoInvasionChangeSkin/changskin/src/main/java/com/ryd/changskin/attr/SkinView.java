package com.ryd.changskin.attr;

import android.view.View;
import java.util.List;

/**
 * 当前View和此View所支持的换肤属性
 */
public class SkinView {
    //    SoftReference<View> viewRef;
    public View view;
    public List<SkinAttr> attrs;

    public SkinView(View view, List<SkinAttr> skinAttrs) {
        this.view = view;
        this.attrs = skinAttrs;
    }

    public void apply() {
        // View view = viewRef.get();
        if (view == null) return;

        for (SkinAttr attr : attrs) {
            attr.apply(view);
        }
    }
}
