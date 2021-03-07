package derson.com.multipletheme.colorUi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import derson.com.multipletheme.colorUi.ColorUiInterface;
import derson.com.multipletheme.colorUi.util.ViewAttributeUtil;


public class ColorRelativeLayout extends RelativeLayout implements ColorUiInterface{

    private int attr_background = -1;

    public ColorRelativeLayout(Context context) {
        super(context);
    }

    public ColorRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr_background = ViewAttributeUtil.getBackgroundAttibute(attrs);
        // ColorRelativeLayout2: attr_background 2130771968
        Log.d("ruanyandong", "ColorRelativeLayout2: attr_background "+attr_background);
    }

    public ColorRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr_background = ViewAttributeUtil.getBackgroundAttibute(attrs);
        Log.d("ruanyandong", "ColorRelativeLayout3: attr_background "+attr_background);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTheme(Resources.Theme themeId) {
        if(attr_background != -1) {
            ViewAttributeUtil.applyBackgroundDrawable(this, themeId, attr_background);
        }
    }
}
