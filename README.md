# Android-dynamic-change-skin
Android动态换肤方案集合，MultipleTheme是静态换肤方案，其他都是动态换肤方案

# 换肤简介
`换肤本质`上是对资源的一种替换包括、字体、颜色、背景、图片、大小等等。比如View的修改背景颜色setBackgroundColor,TextView的setTextSize修改字体等等。
# 换肤方案
目前Android换肤有两种类型，`静态换肤`和`动态换肤`；静态换肤就是将所有的皮肤方案放到项目中，而动态换肤则就是从网络加载皮肤包动态切换；

 - 通常静态换肤是通过Theme实现，通过在项目中定义多套主题，使用setTheme方法切换的方式实现换肤；
 - 动态换肤是通过替换系统的Resouce动态加载下载到本地的资源包实现换肤。

下面我们对这个两种种换肤方式进行讲解。
# 静态换肤之Theme换肤
这种方式是谷歌官方推荐的方式，很多google的app都是使用的这种方式，据说知乎也是使用的这种方式，这种方式的优点就是使用很简单，首先在res/values/attrs.xml下定义属性名称和类型

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <attr name="main_bg" format="reference|color"/>
    <attr name="main_textcolor" format="reference|color"/>
    <attr name="main_textAppearance" format="reference|color"/>
    <attr name="main_btn_bg" format="reference|color"/>
    <attr name="second_bg" format="reference|color"/>
    <attr name="second_textcolor" format="reference|color"/>
</resources>
```
接着准备一些资源，这里以color资源举例，在res/values/colors.xml中定义一些color：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="bg_main_normal">#ffffcc</color>
    <color name="textcolor_main_normal">#ff0000</color>
    <color name="bg_main_dark">#000000</color>
    <color name="textcolor_main_dark">#33ffff</color>
    <color name="bg_second_normal">#0000ff</color>
    <color name="textcolor_second_normal">#00ff00</color>
    <color name="bg_second_dark">#ffffff</color>
    <color name="textcolor_second_dark">#000000</color>
    <color name="main_textAppearance">#000000</color>
    <color name="drak_textAppearance">#ffffff</color>
</resources>
```
然后在定义两套主题，分别引用不同的颜色资源：

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="theme_1" >
        <item name="main_bg">@color/bg_main_normal</item>
        <item name="main_textcolor">@color/textcolor_main_normal</item>
        <item name="main_btn_bg">@color/textcolor_main_dark</item>
        <item name="main_textAppearance">@style/theme_1_btn</item>
        <item name="second_bg">@color/bg_second_normal</item>
        <item name="second_textcolor">@color/textcolor_second_normal</item>
    </style>

    <style name="theme_2">
        <item name="main_bg">@color/bg_main_dark</item>
        <item name="main_textcolor">@color/textcolor_main_dark</item>
        <item name="main_btn_bg">@color/textcolor_main_normal</item>
        <item name="main_textAppearance">@style/theme_2_btn</item>
        <item name="second_bg">@color/bg_second_dark</item>
        <item name="second_textcolor">@color/textcolor_second_dark</item>
    </style>
    
</resources>
```
接着在布局文件中通过下面的方式引用资源文件

```xml
android:background="?attr/main_bg"
```
最后在Activity的setContentView方法前设置想要的主题就可以了，注意，设置主题一定要在setContentView方法前，否则不起作用。
   

```java
 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setTheme方法要在setContentView之前调用
        if(SharedPreferencesMgr.getInt("theme", 0) == 1) {
            setTheme(R.style.theme_2);
        } else {
            setTheme(R.style.theme_1);
        }
        setContentView(R.layout.activity_main2);
    }
```
这里有几个问题：
1.当我们在应用中切换主题时只有重新创建的Activity才会使用新的主题，所以这里就需要我们手动调用一下`recreate()`方法，或者重启应用，代码如下

```java
Intent intent = new Intent(this, MainActivity.class);
intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
startActivity(intent);
// 杀掉当前进程，这行代码也只能杀当前进程，杀不了其他进程
android.os.Process.killProcess(android.os.Process.myPid());
// 退出Java虚拟机，进程也就退出了，好像不加也可以
System.exit(0);
```

这样就可以重新创建页面切换主题，但是这样做有两个弊端，一是屏幕会出现一下闪烁，二是页面重新创建之后要考虑数据的恢复。这个问题也是这个方案最大的弊端。

我们可以不重新创建Activity而是在切换主题后手动修改已创建Activity的View的颜色等信息，但是我们不能在每个页面都写上修改页面View信息的方法，这样的工作量太大了，我们把切换主题的页面入口放到最底层，也就是说如果你想切换主题必须回到主页面，这样我们只需要在主页面添加这样的方法就可以了，这也算一个取巧的方法。然后为每个需要换肤的View进行封装，这样做会很麻烦，代码如下：

```java
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
```

```java
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
```

2.如果一个老项目想用这个方案就很难受了，因为除了要定义多套资源外，我们还要把布局文件中的资源引用全部修改一遍，这样做会很麻烦。

代码具体参考[ dersoncheng /MultipleTheme ](https://github.com/stven0king/MultipleTheme)或者[ stven0king /MultipleTheme ](https://github.com/stven0king/MultipleTheme)或者[Android-dynamic-change-skin/MultipleTheme](https://github.com/ruanyandong/Android-dynamic-change-skin/tree/master/MultipleTheme)

# 动态换肤之Resource换肤（插件是换肤）
动态换肤的一般步骤为：

   
 1. 制作皮肤包
 2. 下载并加载皮肤包，拿到皮肤包Resource对象
 3. 标记需要换肤的View
 4.  缓存需要换肤的View
 5. 切换时即时刷新页面

#### 1、制作皮肤包
   制作皮肤包比较简单，在Android Studio中创建一个Application模块，删除里面的Java代码，把需要的资源文件放到res对应的文件夹里
   ![在这里插入图片描述](https://img-blog.csdnimg.cn/20210307194131389.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0NjgxNTgw,size_16,color_FFFFFF,t_70#pic_center)然后build项目，点击Build->Make Project，生成皮肤包apk文件，名字就叫skin_plugin.apk，然后放到sd卡根目录下面，路径就是
```java
Environment.getExternalStorageDirectory() + File.separator + "skin_plugin.apk"  
```
到这里皮肤包就制作好了。
#### 2、下载并加载皮肤包，拿到皮肤包Resource对象
因为我们已经把皮肤包放到sd卡的根目录了，所以就不需要下载了，我们直接加载皮肤插件apk包。

```java
private void loadPlugin(String skinPath, String skinPkgName, String suffix) throws Exception {
        //checkPluginParams(skinPath, skinPkgName);
        AssetManager assetManager = AssetManager.class.newInstance();
        Method addAssetPath = assetManager.getClass().getMethod("addAssetPath", String.class);
        addAssetPath.invoke(assetManager, skinPath);

        Resources superRes = mContext.getResources();
        mResources = new Resources(assetManager, superRes.getDisplayMetrics(), superRes.getConfiguration());
        mResourceManager = new ResourceManager(mResources, skinPkgName, suffix);
        usePlugin = true;
    }
    
```
`skinPath`是插件包在sd卡的路径, `skinPkgName`是插件包的包名, `suffix`是插件资源的后缀。

构造一个`AssetManager`对象并调用addAssetPath方法设置资源文件的路径，由于addAssetPath方法是hide注解的，我们不能直接调用，所以我们通过反射来调用这个方法，最后使用我们构造的AssetManager对象和原来的DisplayMetrics、Configuration构造一个Resources对象。

拿到Resoures对象通过下面的方法获取需要的资源：

```java
getIdentifier(String name, String defType, String defPackage)
```

第一个参数是资源的名称，比如R.color.red，其中red就是name
第二个参数是资源类型，比如R.String.appname，其中String就是类型
第三个参数是资源所在的包名，这是打皮肤包设置的，一般是自己应用的包名

下面是资源获取的类
 

```java
public class ResourceManager {

    /**
     * 资源类型
     */
    private static final String DEFTYPE_DRAWABLE = "drawable";
    private static final String DEFTYPE_COLOR = "color";
    private final Resources mResources;
    /**
     * 插件包名
     */
    private final String mPluginPackageName;
    /**
     * 资源后缀
     */
    private final String mSuffix;


    public ResourceManager(Resources res, String pluginPackageName, String suffix) {
        mResources = res;
        mPluginPackageName = pluginPackageName;
        if (suffix == null) {
            suffix = "";
        }
        mSuffix = suffix;
    }

    /**
     * 获取drawable资源
     * @param name
     * @return
     */
    public Drawable getDrawableByName(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_DRAWABLE, mPluginPackageName));
        } catch (Resources.NotFoundException e) {
            try {
                return mResources.getDrawable(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));
            } catch (Resources.NotFoundException e2) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 获取color资源
     * @param name
     * @return
     */
    public int getColor(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getColor(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 获取colorSelect
     * @param name
     * @return
     */
    public ColorStateList getColorStateList(String name) {
        try {
            name = appendSuffix(name);
            XLog.e("name = " + name);
            return mResources.getColorStateList(mResources.getIdentifier(name, DEFTYPE_COLOR, mPluginPackageName));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 拼接资源名称后缀
     * @param name
     * @return
     */
    private String appendSuffix(String name) {
        if (!TextUtils.isEmpty(mSuffix))
            return name + ("_" + mSuffix);
        return name;
    }

}
```

##### 3、标记需要换肤的View
这里有两种办法。

 **1. 第一种**

就是在布局文件里给直接给需要换肤的view设置tag属性，tag属性以`skin：`开头，后面紧跟`资源名称`和`属性名称`，例如：`skin:left_menu_icon:src`，
对于一个View多个属性需要换肤的，`android:tag="skin:item_text_color:textColor|skin:icon:src"`
同样使用|进行分隔，如果是动态添加的view直接使用`setTag`方法进行标记。


```java
 <ImageView
        android:id="@+id/id_iv_icon"
        android:layout_width="42dp"
        android:layout_height="42dp"
        android:layout_centerVertical="true"
        android:layout_marginRight="8dp"
        android:tag="skin:left_menu_icon:src"
        android:src="@drawable/left_menu_icon"/>

 
```

 **2. 还有一种方式**

在布局文件中自定义一个属性，例如：

```java
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    xmlns:skin="http://schemas.android.com/android/skin"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    skin:enable="true" 
    android:background="@color/color_app_bg" >

        <TextView
            android:id="@+id/detail_text"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            skin:enable="true"  />

</RelativeLayout>
```

导入自定义命名空间`xmlns:skin="http://schemas.android.com/android/skin"`，需要换肤的view添加`skin:enable=“true”` 我们自定义的属性，名字什么的都无所谓，true就是需要换肤，false就是不需要换肤。

 ##### 4、缓存需要换肤的View

 - 第一种

 如果是以tag的形式进行标记，则缓存View的方式就是找到每个页面最外层且id为`android.R.id.content`的VIewGroup，遍历所有view，代码如下：

```java
public class SkinAttrSupport {

    /**
     * 判断是否支持该属性换肤
     *
     * @param attrName
     * @return
     */
    private static SkinAttrType getSupportAttrType(String attrName) {
        for (SkinAttrType attrType : SkinAttrType.values()) {
            if (attrType.getAttrType().equals(attrName))
                return attrType;
        }
        return null;
    }

    /**
     * 传入activity，找到content元素，递归遍历所有的子View，根据tag命名，记录需要换肤的View
     *
     * @param activity
     */
    public static List<SkinView> getSkinViews(Activity activity) {
        List<SkinView> skinViews = new ArrayList<SkinView>();
        ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
        addSkinViews(content, skinViews);
        return skinViews;
    }

    /**
     *  根据当前Activity的id为android.R.id.content的控件，递归的遍历所有View，获取对应的换肤属性
     * @param view
     * @param skinViews
     */
    public static void addSkinViews(View view, List<SkinView> skinViews) {
        SkinView skinView = getSkinView(view);
        if (skinView != null) skinViews.add(skinView);

        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;

            for (int i = 0, n = container.getChildCount(); i < n; i++) {
                View child = container.getChildAt(i);
                addSkinViews(child, skinViews);
            }
        }

    }

    /**
     * 根据当前View的tag获取换肤属性和对应的资源名称，然后根据View和换肤属性列表构造SkinView
     * @param view
     * @return
     */
    public static SkinView getSkinView(View view) {
        Object tag = view.getTag(R.id.skin_tag_id);
        if (tag == null) {
            tag = view.getTag();
        }
        if (tag == null) return null;
        if (!(tag instanceof String)) return null;
        String tagStr = (String) tag;

        List<SkinAttr> skinAttrs = parseTag(tagStr);
        if (!skinAttrs.isEmpty()) {
            changeViewTag(view);
            return new SkinView(view, skinAttrs);
        }
        return null;
    }

    /**
     * 给当前View设置tag
     * @param view
     */
    private static void changeViewTag(View view) {
        Object tag = view.getTag(R.id.skin_tag_id);
        if (tag == null) {
            tag = view.getTag();
            XLog.e("view.getTag() == "+tag);
            view.setTag(R.id.skin_tag_id, tag);
            view.setTag(null);
        }
    }

    /**
     * 根据tag进行解析，获取所有支持换肤的属性放在list中，每个皮肤属性包含 view的属性类型 和 资源名称
     * @param tagStr
     * @return
     */
    //skin:left_menu_icon:src|skin:color_red:textColor
    private static List<SkinAttr> parseTag(String tagStr) {
        List<SkinAttr> skinAttrs = new ArrayList<SkinAttr>();
        if (TextUtils.isEmpty(tagStr)) return skinAttrs;

        String[] items = tagStr.split("[|]");
        for (String item : items) {
            if (!item.startsWith(SkinConfig.SKIN_PREFIX))
                continue;
            String[] resItems = item.split(":");
            if (resItems.length != 3)
                continue;

            String resName = resItems[1];
            String resType = resItems[2];

            SkinAttrType attrType = getSupportAttrType(resType);
            if (attrType == null) continue;
            SkinAttr attr = new SkinAttr(attrType, resName);
            skinAttrs.add(attr);
        }
        return skinAttrs;
    }

}
```
最终所有需要换肤的view缓存在`List<SkinView>`里面，这种方式手机和缓存view没有什么入侵性。

 - 第二种

如果是以`skin：enable=“true”`方式进行标记的话，则收集和缓存的view的方式就是继承`LayoutInflater.Factory2`或者`LayoutInflater.Factory`，重写`onCreateView`方法，把这个Fractory设置给LayoutInflater，代替系统创建view的过程，在此过程中拿到所有需要换肤的view，代码如下：

```java
public class SkinInflaterFactory implements Factory2 {
	
	private static final boolean DEBUG = true;
	
	/**
	 * Store the view item that need skin changing in the activity
	 */
	private List<SkinItem> mSkinItems = new ArrayList<SkinItem>();
	
	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		// if this is NOT enable to be skined , simplly skip it 
		boolean isSkinEnable = attrs.getAttributeBooleanValue(SkinConfig.NAMESPACE, SkinConfig.ATTR_SKIN_ENABLE, false);
        if (!isSkinEnable){
        		return null;
        }
		
		View view = createView(context, name, attrs);
		
		if (view == null){
			return null;
		}
		
		parseSkinAttr(context, attrs, view);
		
		return view;
	}
	
	/**
     * Invoke low-level function for instantiating a view by name. This attempts to
     * instantiate a view class of the given <var>name</var> found in this
     * LayoutInflater's ClassLoader.
     * 
     * @param context 
     * @param name The full name of the class to be instantiated.
     * @param attrs The XML attributes supplied for this instance.
     * 
     * @return View The newly instantiated view, or null.
     */
	private View createView(Context context, String name, AttributeSet attrs) {
		View view = null;
		try {
			if (-1 == name.indexOf('.')){
				if ("View".equals(name)) {
					view = LayoutInflater.from(context).createView(name, "android.view.", attrs);
				} 
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.widget.", attrs);
				} 
				if (view == null) {
					view = LayoutInflater.from(context).createView(name, "android.webkit.", attrs);
				} 
			}else {
	            view = LayoutInflater.from(context).createView(name, null, attrs);
	        }

			L.i("about to create " + name);

		} catch (Exception e) { 
			L.e("error while create 【" + name + "】 : " + e.getMessage());
			view = null;
		}
		return view;
	}

	/**
	 * Collect skin able tag such as background , textColor and so on
	 * 
	 * @param context
	 * @param attrs
	 * @param view
	 */
	private void parseSkinAttr(Context context, AttributeSet attrs, View view) {
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		
		for (int i = 0; i < attrs.getAttributeCount(); i++){
			String attrName = attrs.getAttributeName(i);
			String attrValue = attrs.getAttributeValue(i);
			
			if(!AttrFactory.isSupportedAttr(attrName)){
				continue;
			}
			
		    if(attrValue.startsWith("@")){
				try {
					int id = Integer.parseInt(attrValue.substring(1));
					String entryName = context.getResources().getResourceEntryName(id);
					String typeName = context.getResources().getResourceTypeName(id);
					SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
					if (mSkinAttr != null) {
						viewAttrs.add(mSkinAttr);
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (NotFoundException e) {
					e.printStackTrace();
				}
		    }
		}
		
		if(!ListUtils.isEmpty(viewAttrs)){
			SkinItem skinItem = new SkinItem();
			skinItem.view = view;
			skinItem.attrs = viewAttrs;

			mSkinItems.add(skinItem);
			
			if(SkinManager.getInstance().isExternalSkin()){
				skinItem.apply();
			}
		}
	}
	
	public void applySkin(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.apply();
		}
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, List<DynamicAttr> pDAttrs){	
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		
		for(DynamicAttr dAttr : pDAttrs){
			int id = dAttr.refResId;
			String entryName = context.getResources().getResourceEntryName(id);
			String typeName = context.getResources().getResourceTypeName(id);
			SkinAttr mSkinAttr = AttrFactory.get(dAttr.attrName, id, entryName, typeName);
			viewAttrs.add(mSkinAttr);
		}
		
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void dynamicAddSkinEnableView(Context context, View view, String attrName, int attrValueResId){	
		int id = attrValueResId;
		String entryName = context.getResources().getResourceEntryName(id);
		String typeName = context.getResources().getResourceTypeName(id);
		SkinAttr mSkinAttr = AttrFactory.get(attrName, id, entryName, typeName);
		SkinItem skinItem = new SkinItem();
		skinItem.view = view;
		List<SkinAttr> viewAttrs = new ArrayList<SkinAttr>();
		viewAttrs.add(mSkinAttr);
		skinItem.attrs = viewAttrs;
		addSkinView(skinItem);
	}
	
	public void addSkinView(SkinItem item){
		mSkinItems.add(item);
	}
	
}
```
把所有view收集在`List<SkinItem> mSkinItems`中，同时在对应的BaseActivity中把这个Fractory设置LayoutInflater：


```java
@Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory2(layoutInflater, new SkinInflaterFactory());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
```

**为什么可以这样写呢**？因为在`super.onCreate(savedInstanceState);`这行代码的内部源码处，会去判断是否设置过Factory2，没有设置则会去创建并设置，然后在`setContentView(R.layout.activity_main);`内部会去解析布局XML文件，调用已经设置给`LayoutInflater`的`Factory2`的onCreateView创建对应的View，如果我们在系统设置`Factory2`给`LayoutInflater`之前就把我们自定义的Fractory设置给了`LayoutInflater`，那么系统就不会再设置Factory了，我们便代替系统创建了view。

这样做入侵性比较强，如果系统内部有更新改动，我们的方案很有可能就不行了。

 ##### 5. 切换时即时刷新页面

 - tag方式标记，换肤刷新页面

```java
/**
     * 对当前Activity进行换肤
     * @param activity
     */
    public void apply(Activity activity) {
        List<SkinView> skinViews = SkinAttrSupport.getSkinViews(activity);
        if (skinViews == null) return;
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }

    /**
     * 对当前Activity进行换肤
     * @param activity
     */
    public void register(final Activity activity) {
        mActivities.add(activity);

        activity.findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                apply(activity);
            }
        });
    }

    /**
     * 移除当前换肤的Activity
     * @param activity
     */
    public void unregister(Activity activity) {
        mActivities.remove(activity);
    }

    /**
     * 对所有注册了的Activity进行焕肤
     */
    public void notifyChangedListeners() {

        for (Activity activity : mActivities) {
            apply(activity);
        }
    }

    /**
     * 对当前View和其子View进行焕肤
     *
     * @param view
     */
    public void injectSkin(View view) {
        List<SkinView> skinViews = new ArrayList<SkinView>();
        SkinAttrSupport.addSkinViews(view, skinViews);
        for (SkinView skinView : skinViews) {
            skinView.apply();
        }
    }
```

 - skin：enable方式标记换肤刷新页面
 
 这里贴出部分代码，具体请参考文后的demo链接。

```java
public void applySkin(){
		if(ListUtils.isEmpty(mSkinItems)){
			return;
		}
		
		for(SkinItem si : mSkinItems){
			if(si.view == null){
				continue;
			}
			si.apply();
		}
	}

public void apply(){
		if(ListUtils.isEmpty(attrs)){
			return;
		}
		for(SkinAttr at : attrs){
			at.apply(view);
		}
	}

public class BackgroundAttr extends SkinAttr {

	@Override
	public void apply(View view) {
		
		if(RES_TYPE_NAME_COLOR.equals(attrValueTypeName)){
			view.setBackgroundColor(SkinManager.getInstance().getColor(attrValueRefId));
		}else if(RES_TYPE_NAME_DRAWABLE.equals(attrValueTypeName)){
			Drawable bg = SkinManager.getInstance().getDrawable(attrValueRefId);
			view.setBackground(bg);
		
		}
	}
}

```

动态换肤到这里就讲完了，下面附上了代码链接，有兴趣可自行参阅。
 

代码具体参考[InvasionChangeSkin](https://github.com/ruanyandong/Android-dynamic-change-skin/tree/master/InvasionChangeSkin)或者[NoInvasionChangeSkin](https://github.com/ruanyandong/Android-dynamic-change-skin/tree/master/NoInvasionChangeSkin)或者[Android-Skin-Loader](https://github.com/ruanyandong/Android-dynamic-change-skin/tree/master/Android-Skin-Loader)

参考文章

[Android换肤总结](https://blog.csdn.net/shanshui911587154/article/details/104820919/)

[Android换肤原理和Android-Skin-Loader框架解析](https://cloud.tencent.com/developer/article/1634772)

[Android 切换主题以及换肤的实现](https://www.cnblogs.com/likeandroid/p/4501758.html)
















