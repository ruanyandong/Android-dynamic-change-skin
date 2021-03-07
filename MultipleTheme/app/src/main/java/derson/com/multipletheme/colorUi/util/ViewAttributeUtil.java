package derson.com.multipletheme.colorUi.util;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import derson.com.multipletheme.colorUi.ColorUiInterface;

public class ViewAttributeUtil {

    public static int getAttributeValue(AttributeSet attr, int paramInt) {
        int value = -1;
        int count = attr.getAttributeCount();

        //getAttributeValue: attr.getAttributeCount() 7 paramInt 16842904
        Log.d("ruanyandong", "getAttributeValue: attr.getAttributeCount() "+attr.getAttributeCount()+" paramInt "+paramInt);
        for(int i = 0; i <count;i++) {
            if(attr.getAttributeNameResource(i) == paramInt) {
                //str ?2130771968
                String str = attr.getAttributeValue(i);

                //getAttributeValue: attr.getAttributeName(i) background  attr.getAttributeValue(i) ?2130771968
                //getAttributeValue: attr.getAttributeName(i) textColor  attr.getAttributeValue(i) ?2130771971
                Log.d("ruanyandong", "getAttributeValue: attr.getAttributeName(i) "+attr.getAttributeName(i)+" attr.getAttributeValue(i) "+attr.getAttributeValue(i));
                if(null != str && str.startsWith("?")) {
                    value = Integer.parseInt(str.substring(1));
                    return value;
                }
            }
        }
        return value;
    }

    public static int getBackgroundAttibute(AttributeSet attr) {
        return getAttributeValue(attr , android.R.attr.background);
    }

    public static int getCheckMarkAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.checkMark);
    }

    public static int getSrcAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.src);
    }

    public static int getTextApperanceAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.textAppearance);
    }

    public static int getDividerAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.divider);
    }

    public static int getTextColorAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.textColor);
    }

    public static void applyBackgroundDrawable(ColorUiInterface ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        Drawable drawable = ta.getDrawable(0);
        if(null != ci) {
            (ci.getView()).setBackgroundDrawable(drawable);
        }
        ta.recycle();
    }

    public static void applyImageDrawable(ColorUiInterface ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        Drawable drawable = ta.getDrawable(0);
        if(null != ci && ci instanceof ImageView) {
            ((ImageView)ci.getView()).setImageDrawable(drawable);
        }
        ta.recycle();
    }

    public static void applyTextAppearance(ColorUiInterface ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        int resourceId = ta.getResourceId(0,0);
        if(null != ci && ci instanceof TextView) {
            ((TextView)ci.getView()).setTextAppearance(ci.getView().getContext(), resourceId);
        }
        ta.recycle();
    }

    public static void applyTextColor(ColorUiInterface ci, Resources.Theme theme, int paramInt) {
        TypedArray ta = theme.obtainStyledAttributes(new int[]{paramInt});
        int resourceId = ta.getColor(0,0);
        if(null != ci && ci instanceof TextView) {
            ((TextView)ci.getView()).setTextColor(resourceId);
        }
        ta.recycle();
    }

}
