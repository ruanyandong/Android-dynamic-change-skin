package derson.com.multipletheme.colorUi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import derson.com.multipletheme.colorUi.ColorUiInterface;
import derson.com.multipletheme.colorUi.util.ViewAttributeUtil;

public class ColorButton extends Button implements ColorUiInterface{

    private int attr_background = -1;
    private int attr_textAppreance = -1;
    private int attr_textColor = -1;

    public ColorButton(Context context) {
        super(context);
    }

    public ColorButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr_background = ViewAttributeUtil.getBackgroundAttibute(attrs);
        this.attr_textAppreance = ViewAttributeUtil.getTextApperanceAttribute(attrs);
        this.attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);
        // ColorButton2: attr_background 2130771968 attr_textAppreance -1 attr_textColor 2130771971
        Log.d("ruanyandong", "ColorButton2: attr_background "+attr_background+" attr_textAppreance "+attr_textAppreance+" attr_textColor "+attr_textColor);
    }

    public ColorButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr_background = ViewAttributeUtil.getBackgroundAttibute(attrs);
        this.attr_textAppreance = ViewAttributeUtil.getTextApperanceAttribute(attrs);
        this.attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);
        Log.d("ruanyandong", "ColorButton3: attr_background "+attr_background+" attr_textAppreance "+attr_textAppreance+" attr_textColor "+attr_textColor);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTheme(Resources.Theme themeId) {
        if (attr_textAppreance != -1) {
            ViewAttributeUtil.applyTextAppearance(this, themeId, attr_textAppreance);
        }
        if (attr_background != -1) {
            ViewAttributeUtil.applyBackgroundDrawable(this, themeId, attr_background);
        }
        if (attr_textColor != -1) {
            ViewAttributeUtil.applyTextColor(this, themeId, attr_textColor);
        }
    }

    @Override
    public void setBackgroundResource(int resid) {
        Log.d("ruanyandong", "setBackgroundResource: resid "+resid);
        // setBackgroundResource: resid 2130771969
        attr_background = resid;
        ViewAttributeUtil.applyBackgroundDrawable(this, getContext().getTheme(), attr_background);
    }
}
