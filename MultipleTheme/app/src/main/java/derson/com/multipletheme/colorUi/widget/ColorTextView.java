package derson.com.multipletheme.colorUi.widget;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import derson.com.multipletheme.colorUi.ColorUiInterface;
import derson.com.multipletheme.colorUi.util.ViewAttributeUtil;

public class ColorTextView extends TextView implements ColorUiInterface {

    private int attr_drawable = -1;
    private int attr_textAppearance = -1;
    private int attr_textColor = -1;

    // //    在代码中直接new一个Custom View实例的时候,会调用一个参数构造函数.这个没有任何争议.
    ////    在xml布局文件中调用Custom View的时候,会调用二个参数构造函数.这个也没有争议.
    ////    在xml布局文件中调用Custom View,并且Custom View标签中还有自定义属性时,这里调用的还是二个参数构造函数.
    ////
    ////    也就是说,系统默认只会调用Custom View的前两个构造函数,至于第三个构造函数的调用,通常是我们自己在构造函数中主动调用的（例如,在第二个构造函数中调用第三个构造函数）.
    public ColorTextView(Context context) {
        super(context);
    }

    //    AttributeSet attrs: 属性值的集合.
    //    int[] attrs: 我们自定义属性集合在R类中生成的int型数组.这个数组中包含了自定义属性的资源ID.
    //    int defStyleAttr: 这是当前Theme中的包含的一个指向style的引用.当我们没有给自定义View设置declare-styleable资源集合时,默认从这个集合里面查找布局文件中配置属性值.传入0表示不向该defStyleAttr中查找默认值.
    //    int defStyleRes: 这个也是一个指向Style的资源ID,但是仅在defStyleAttr为0或者defStyleAttr不为0但Theme中没有为defStyleAttr属性赋值时起作用.

    // 在布局xml中直接定义 > 在布局xml中通过style定义 > 自定义View所在的Activity的Theme中指定style引用 > 构造函数中defStyleRes指定的默认值
    public ColorTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr_textAppearance = ViewAttributeUtil.getTextApperanceAttribute(attrs);
        this.attr_drawable = ViewAttributeUtil.getBackgroundAttibute(attrs);
        this.attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);

        //<derson.com.multipletheme.colorUi.widget.ColorTextView
        //        android:textColor="?attr/main_textcolor"
        //        android:layout_width="wrap_content"
        //        android:layout_height="wrap_content"
        //        android:text="@string/hello_world" />

        // ColorTextView2: attr_textAppearance -1 attr_drawable -1 attr_textColor 2130771971
        Log.d("ruanyandong", "ColorTextView2: attr_textAppearance "+attr_textAppearance+" attr_drawable "+attr_drawable+" attr_textColor "+attr_textColor);
    }


    public ColorTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr_textAppearance = ViewAttributeUtil.getTextApperanceAttribute(attrs);
        this.attr_drawable = ViewAttributeUtil.getBackgroundAttibute(attrs);
        this.attr_textColor = ViewAttributeUtil.getTextColorAttribute(attrs);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void setTheme(Resources.Theme themeId) {
        if (attr_drawable != -1) {
            ViewAttributeUtil.applyBackgroundDrawable(this, themeId, attr_drawable);
        }
        if(attr_textAppearance != -1) {
            ViewAttributeUtil.applyTextAppearance(this, themeId, attr_textAppearance);
        }
        if (attr_textColor != -1) {
            ViewAttributeUtil.applyTextColor(this, themeId, attr_textColor);
        }
    }
}
