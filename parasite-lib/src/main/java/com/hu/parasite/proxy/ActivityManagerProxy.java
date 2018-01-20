package com.hu.parasite.proxy;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.hu.parasite.ParasiteApplication;
import com.hu.parasite.hook.Hookable;
import com.hu.parasite.util.LogUtil;
import com.hu.parasite.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HuJi on 2018/1/21.
 */

public class ActivityManagerProxy implements Hookable, InvocationHandler {

    private static final String TAG = ActivityManagerProxy.class.getSimpleName();
    private static Application sApplication = ParasiteApplication.getInstance();

    private Object mActivityManager = null;

    @Override
    public void hook() throws Throwable {
        // 保存原来的ActivityManager
        Object gDefault = ReflectUtil.get("android.app.ActivityManagerNative", "gDefault");
        mActivityManager = ReflectUtil.get(gDefault, "mInstance");

        // 生成代理类
        Object proxy = Proxy.newProxyInstance(sApplication.getClassLoader(), mActivityManager.getClass().getInterfaces(), this);

        // 替换gDefault中的mInstance
        ReflectUtil.set(gDefault, "mInstance", proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // LogUtil.d(TAG, "ActivityManager.%s", method.getName());
        return method.invoke(mActivityManager, args);
    }
}
