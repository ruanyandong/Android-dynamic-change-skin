package com.ryd.changeskin.base;

import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.LayoutInflaterCompat;

import com.ryd.changeskin.SkinManager;
import com.ryd.changeskin.attr.SkinAttr;
import com.ryd.changeskin.attr.SkinAttrSupport;
import com.ryd.changeskin.attr.SkinView;
import com.ryd.changeskin.callback.ISkinChangedListener;
import com.ryd.changeskin.utils.XLog;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BaseSkinActivity extends AppCompatActivity implements ISkinChangedListener, LayoutInflater.Factory2 {

    static final Class<?>[] sConstructorSignature = new Class[]{Context.class, AttributeSet.class};
    private static final Map<String, Constructor<? extends View>> sConstructorMap = new ArrayMap<>();
    private final Object[] mConstructorArgs = new Object[2];
    private static Method sCreateViewMethod;
    static final Class<?>[] sCreateViewSignature = new Class[]{View.class, String.class, Context.class, AttributeSet.class};

    // LayoutInflater的inflate方法中会调用Factory2的onCreateView创建View
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {

        LayoutInflater layoutInflater = getLayoutInflater();
        AppCompatDelegate delegate = getDelegate();
        View view = null;
        try {
            //public View createView
            // (View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs)
            if (sCreateViewMethod == null) {
                //  /**
                //     * From {@link LayoutInflater.Factory2}.
                //     */
                //    @SuppressWarnings("NullableProblems")
                //    @Override
                //    public final View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                //        return createView(parent, name, context, attrs);
                //    }
                // 调用AppCompatDelegate的实现类AppCompatDelegateImpl的createView方法创建View
                Method methodOnCreateView = delegate.getClass().getMethod("createView", sCreateViewSignature);
                sCreateViewMethod = methodOnCreateView;
            }
            // 代替系统创建View
            Object object = sCreateViewMethod.invoke(delegate, parent, name, context, attrs);
            view = (View) object;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

//        if ("fragment".equals(name))
//        {
//            view = super.onCreateView(name, context, attrs);
//        }
//        final boolean isPre21 = Build.VERSION.SDK_INT < 21;
//
//        if (mAppCompatViewInflater == null)
//        {
//            mAppCompatViewInflater = new AppCompatViewInflater();
//        }
//
//        boolean subDecorInstalled = true;
//        final boolean inheritContext = isPre21 && subDecorInstalled && parent != null
//                && parent.getId() != android.R.id.content
//                && !ViewCompat.isAttachedToWindow(parent);
//
//        view = mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
//                isPre21,
//                true);
//
        XLog.e("开始收集皮肤");
        //  收集当前View在换肤框架中所支持的属性
        List<SkinAttr> skinAttrList = SkinAttrSupport.getSkinAttrs(attrs, context);
        if (skinAttrList.isEmpty()) {
            return view;
        }
        if (view == null) {
            // 系统创建view的过程
            view = createViewFromTag(context, name, attrs);
        }
        injectSkin(view, skinAttrList);
        return view;

    }

    private void injectSkin(View view, List<SkinAttr> skinAttrList) {
        //do some skin inject
        if (skinAttrList.size() != 0) {
            List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
            if (skinViews == null) {
                skinViews = new ArrayList<>();
            }
            SkinManager.getInstance().addSkinView(this, skinViews);
            skinViews.add(new SkinView(view, skinAttrList));

            if (SkinManager.getInstance().needChangeSkin()) {
                SkinManager.getInstance().apply(this);
            }
        }
    }


    private View createViewFromTag(Context context, String name, AttributeSet attrs) {
        if (name.equals("view")) {
            name = attrs.getAttributeValue(null, "class");
        }

        try {
            mConstructorArgs[0] = context;
            mConstructorArgs[1] = attrs;

            // 系统自带的view控件在xml文件里不带包名
            if (-1 == name.indexOf('.')) {
                // try the android.widget prefix first...
                return createView(context, name, "android.widget.");
            } else {
                return createView(context, name, null);
            }
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        } finally {
            // Don't retain references on context.
            mConstructorArgs[0] = null;
            mConstructorArgs[1] = null;
        }
    }

    // 参阅LayoutInflater的createView方法，通过反射创建view控件的实例
    private View createView(Context context, String name, String prefix)
            throws ClassNotFoundException, InflateException {
        Constructor<? extends View> constructor = sConstructorMap.get(name);

        try {
            if (constructor == null) {
                // Class not found in the cache, see if it's real, and try to add it
                Class<? extends View> clazz = context.getClassLoader().loadClass(prefix != null ? (prefix + name) : name).asSubclass(View.class);

                constructor = clazz.getConstructor(sConstructorSignature);
                sConstructorMap.put(name, constructor);
            }
            constructor.setAccessible(true);
            return constructor.newInstance(mConstructorArgs);
        } catch (Exception e) {
            // We do not want to catch these, lets return null and let the actual LayoutInflater
            // try
            return null;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory2(layoutInflater, this);
        super.onCreate(savedInstanceState);
        SkinManager.getInstance().addChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SkinManager.getInstance().removeChangedListener(this);
    }


    @Override
    public void onSkinChanged() {
        SkinManager.getInstance().apply(this);
    }
}
