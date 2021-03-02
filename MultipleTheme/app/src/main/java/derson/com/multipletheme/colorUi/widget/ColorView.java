package derson.com.multipletheme.colorUi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.View;
import derson.com.multipletheme.colorUi.ColorUiInterface;
import derson.com.multipletheme.colorUi.util.ViewAttributeUtil;

public class ColorView extends View implements ColorUiInterface {
    //记录当前View的background的资源id
    private int attrBackground = -1;
    public ColorView(Context context) {
        super(context, null);
    }
    public ColorView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }
    public ColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attrBackground = ViewAttributeUtil.getBackgroundAttibute(attrs);
    }
    @Override
    public View getView() {
        return this;
    }
    //更新主题
    @Override
    public void setTheme(Resources.Theme themeId) {
        if(attrBackground != -1) {
            ViewAttributeUtil.applyBackgroundDrawable(this, themeId, attrBackground);
        }
    }
    //更新当前View的background的资源id
    @Override
    public void setBackgroundResource(int resid) {
        attrBackground = resid;
        ViewAttributeUtil.applyBackgroundDrawable(this, getContext().getTheme(), attrBackground);
    }
}
