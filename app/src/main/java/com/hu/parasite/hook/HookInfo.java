package com.hu.parasite.hook;

import java.lang.reflect.Method;

/**
 * Created by HuJi on 2018/1/15.
 */

public class HookInfo {

    public Method mOrigin;
    public Method mHook;

    public HookInfo(Method origin, Method hook) {
        mOrigin = origin;
        mHook = hook;
    }
}
