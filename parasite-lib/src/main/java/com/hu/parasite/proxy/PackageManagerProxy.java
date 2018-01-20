package com.hu.parasite.proxy;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.hu.parasite.ParasiteApplication;
import com.hu.parasite.hook.Hookable;
import com.hu.parasite.util.ReflectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by HuJi on 2018/1/17.
 */

public class PackageManagerProxy implements Hookable, InvocationHandler {

    private static final String TAG = PackageManagerProxy.class.getSimpleName();
    private static Application sApplication = ParasiteApplication.getInstance();
    private static ApplicationInfo sApplicationInfo = sApplication.getApplicationInfo();

    private Object mPackageManager = null;

    @Override
    public void hook() throws Throwable {
        // 保存原来的PackageManager
        Object currentActivityThread = ReflectUtil.invoke("android.app.ActivityThread", "currentActivityThread");
        mPackageManager = ReflectUtil.get(currentActivityThread, "sPackageManager");

        // 生成代理类
        Object proxy = Proxy.newProxyInstance(sApplication.getClassLoader(), mPackageManager.getClass().getInterfaces(), this);

        // 替换currentActivityThread中的sPackageManager
        ReflectUtil.set(currentActivityThread, "sPackageManager", proxy);

        // 替换 ApplicationPackageManager中的mPM
        PackageManager packageManager = sApplication.getPackageManager();
        ReflectUtil.set(packageManager, "mPM", proxy);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = method.invoke(mPackageManager, args);
        if (method.getName().equals("getApplicationInfo")) {
            if (args[0].equals(sApplication.getPackageName())) {
                ApplicationInfo applicationInfo = (ApplicationInfo) result;
                applicationInfo.sourceDir = sApplicationInfo.sourceDir;
                applicationInfo.publicSourceDir = sApplicationInfo.publicSourceDir;
            }
        }
        return result;
    }
}