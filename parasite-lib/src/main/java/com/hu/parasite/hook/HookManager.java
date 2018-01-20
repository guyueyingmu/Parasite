package com.hu.parasite.hook;

import com.hu.parasite.annotation.TargetMethod;
import com.hu.parasite.util.ReflectUtil;

import java.lang.reflect.Method;

/**
 * Created by HuJi on 2017/12/31.
 */

public class HookManager {

    static {
        System.loadLibrary("parasite-lib");
    }

    private static final String TAG = HookManager.class.getName();
    private static volatile HookManager sInstance = null;

    public static HookManager getInstance() {
        if (sInstance == null){
            synchronized (HookManager.class) {
                if (sInstance == null){
                    sInstance = new HookManager();
                }
            }
        }
        return sInstance;
    }

    private final HookHelper mHookHelper = HookHelper.getsInstance();

    public void hook(Class<?> clazz) throws Exception {
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(TargetMethod.class)) {
                String[] values = method.getAnnotation(TargetMethod.class).value().split("##");
                if (values.length >= 3) {
                    Method target = ReflectUtil.getMethod(values[0], values[1], values[2].split(","));
                    hook(target, method);
                } else {
                    Method target = ReflectUtil.getMethod(values[0], values[1]);
                    hook(target, method);
                }
            }
        }
    }

    public synchronized void hook(Method origin, Method hook) throws HookException {
        HookInfo hookInfo = mHookHelper.getByOrigin(origin);
        if (hookInfo != null) {
            throw new HookException("%s::%s has been hooked",
                    origin.getDeclaringClass().getName(), origin.getName());
        }
        mHookHelper.put(origin, hook);
        nativeHook(origin, hook);
    }

    public synchronized void unhook(Method origin) throws HookException {
        HookInfo hookInfo = mHookHelper.getByOrigin(origin);
        if (hookInfo == null) {
            throw new HookException("%s::%s has not been hooked",
                    origin.getDeclaringClass().getName(), origin.getName());
        }
        mHookHelper.removeByOrigin(origin);
        nativeUnhook(origin, false);
    }

    public synchronized Object invoke(Object object, Object... args) throws Exception {
        StackTraceElement element = new Throwable().getStackTrace()[1];
        HookInfo hookInfo = mHookHelper.getByHook(element.getClassName(), element.getMethodName(), args);
        if (hookInfo == null) {
            throw new HookException("%s::%s has not been hooked",
                    element.getClassName(), element.getMethodName());
        }
        nativeUnhook(hookInfo.getOrigin(), true);
        Object result = hookInfo.getOrigin().invoke(object, args);
        nativeHook(hookInfo.getOrigin(), hookInfo.getHook());
        return result;
    }

    private native void nativeHook(Method oldMethod, Method newMethod);
    private native void nativeUnhook(Method method, boolean cache);

    private HookManager() {

    }

    private static class Methods {
        public static int f1(int n1, int n2) { return n1 + n2; }
        public static native long f2(int n1, int n2, int n3, int n4);
    }
}

