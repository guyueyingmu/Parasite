package com.hu.parasite.hook;

import java.lang.reflect.Method;

/**
 * Created by HuJi on 2018/1/15.
 */

public class HookInfo {

    private Method mOrigin;
    private Method mHook;
    private Class<?>[] mParameterTypes;

    public HookInfo(Method origin, Method hook) {
        mOrigin = origin;
        mHook = hook;
        mParameterTypes = mOrigin.getParameterTypes();
    }

    public Method getOrigin() {
        return mOrigin;
    }

    public Method getHook() {
        return mHook;
    }

    public Class<?>[] getParameterTypes() {
        return mParameterTypes;
    }
}
