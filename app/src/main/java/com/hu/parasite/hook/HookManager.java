package com.hu.parasite.hook;

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

    public static HookManager getsInstance() {
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

    public <T> T invoke(Object object, Object... args) throws Exception {
        StackTraceElement element = new Throwable().getStackTrace()[1];
        HookInfo hookInfo = mHookHelper.getByHook(element.getClassName(), element.getMethodName(), args);
        if (hookInfo == null) {
            throw new HookException("%s::%s has not been hooked",
                    element.getClassName(), element.getMethodName());
        }
        nativeUnhook(hookInfo.mOrigin, true);
        T ret = (T) hookInfo.mOrigin.invoke(object, args);
        nativeHook(hookInfo.mOrigin, hookInfo.mOrigin);
        return (T) ret;
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

