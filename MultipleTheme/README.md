# MultipleTheme

项目在原有的 `MultipleTheme` 进行的优化，增加可以在代码中动态设置资源之后顺利换肤。

真正的支持无缝换肤／夜间模式的Android框架，配合theme和换肤控件框架可以做到无缝切换换肤（无需重启应用和当前页面）。

该应用框架可以实现无缝换肤／切换夜间模式的需求，需要在换肤／切换夜间模式的界面只需要使用框架里的自封装控件，其他界面的控件使用原生`android`控件即可。

# 实现步骤
第一步：在项目的attr.xml声明自定义属性（各种模式都会用到的属性）
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <attr name="main_bg" format="reference|color"/>
    <attr name="main_textcolor" format="reference|color"/>
    <attr name="second_bg" format="reference|color"/>
    <attr name="second_textcolor" format="reference|color"/>
</resources>
```

第二步：在项目的style.xml指定各种模式主题下的自定义属性值
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="theme_1" >
        <item name="main_bg">@color/bg_main_normal</item>
        <item name="main_textcolor">@color/textcolor_main_normal</item>
        <item name="second_bg">@color/bg_second_normal</item>
        <item name="second_textcolor">@color/textcolor_second_normal</item>
    </style>
    <style name="theme_2">
        <item name="main_bg">@color/bg_main_dark</item>
        <item name="main_textcolor">@color/textcolor_main_dark</item>
        <item name="second_bg">@color/bg_second_dark</item>
        <item name="second_textcolor">@color/textcolor_second_dark</item>
    </style>
</resources>
```

第三步：在页面布局文件里使用自定义属性值
```xml
<derson.com.multipletheme.colorUi.widget.ColorTextView
    android:textColor="?attr/main_textcolor"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="@string/hello_world" />

<derson.com.multipletheme.colorUi.widget.ColorButton
    android:id="@+id/btn"
    android:text="换肤"
    android:layout_centerInParent="true"
    android:textColor="?attr/main_textcolor"
    android:layout_width="100dip"
    android:layout_height="80dip" />
```
`?attr/` 开头的资源引用。

第四步：在基类的`onCreate`方法里添加切换主题模式的逻辑代码
```java
public class BaseActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(SharedPreferencesMgr.getInt("theme", 0) == 1) {
            setTheme(R.style.theme_2);
        } else {
            setTheme(R.style.theme_1);
        }
    }
}
```

第五步：调用工具类方法切换主题模式
```java
if(SharedPreferencesMgr.getInt("theme", 0) == 1) {
    SharedPreferencesMgr.setInt("theme", 0);
    setTheme(R.style.theme_1);
} else {
    SharedPreferencesMgr.setInt("theme", 1);
    setTheme(R.style.theme_2);
}
final View rootView = getWindow().getDecorView();
if(Build.VERSION.SDK_INT < 14) {
    ColorUiUtil.changeTheme(rootView, getTheme());
} else {
    //部分代码省略
    //增加动画执行ColorUiUtil.changeTheme(rootView, getTheme());
}
```

`ColorUiUtil.changeTheme(rootView, getTheme());` 遍历 `rootView` 中的子`view`执行`view.apply(theme)` 。

```java
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
```

第六步：针对切换主题模式时需要立即更新页面ui的页面，需要使用框架里的封装控件

```xml
<derson.com.multipletheme.colorUi.widget.ColorTextView
    ……>
<derson.com.multipletheme.colorUi.widget.ColorButton
    ……/>
<derson.com.multipletheme.colorUi.widget.ColorButton
    ……/>
```

这里也可以通过 `LayoutInflater` 在 `xml` 生成 `View` 的过程中替换掉原来的 `View` 。

```java
 private View createViewFromFV(Context context, String name, AttributeSet attrs) {
    View view = null;
    if (name.contains(".")) {
        return null;
    }
    switch (name) {
        case "View":
            view = new SkinCompatView(context, attrs);
            break;
        case "LinearLayout":
            view = new SkinCompatLinearLayout(context, attrs);
            break;
        case "RelativeLayout":
            view = new SkinCompatRelativeLayout(context, attrs);
            break;
        //部分代码省略
        default:
            break;
    }
    return view;
}
```

