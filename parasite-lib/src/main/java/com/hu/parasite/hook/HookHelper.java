package com.hu.parasite.hook;

import com.hu.parasite.util.ReflectUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by HuJi on 2018/1/14.
 */

public class HookHelper {

    private static final String TAG = HookHelper.class.getName();
    private static volatile HookHelper sInstance = null;

    public static HookHelper getsInstance() {
        if (sInstance == null){
            synchronized (HookManager.class) {
                if (sInstance == null){
                    sInstance = new HookHelper();
                }
            }
        }
        return sInstance;
    }

    private HookInfoMap mOriginMap = new HookInfoMap();
    private HookInfoMap mHookMap = new HookInfoMap();

    public void put(Method origin, Method hook) {
        HookInfo info = new HookInfo(origin, hook);
        mOriginMap.put(origin.getDeclaringClass().getName(), origin.getName(), info);
        mHookMap.put(hook.getDeclaringClass().getName(), hook.getName(), info);
    }

    public HookInfo getByOrigin(Method origin) {
        return getByOrigin(
                origin.getDeclaringClass().getName(),
                origin.getName(),
                origin.getParameterTypes());
    }

    public HookInfo getByOrigin(String className, String methodName, Class<?>... types) {
        return mOriginMap.get(className, methodName, types);
    }

    public HookInfo getByOrigin(String className, String methodName, Object... args) {
        return mOriginMap.get(className, methodName, args);
    }

    public HookInfo getByHook(String className, String methodName, Class<?>... types) {
        return mHookMap.get(className, methodName, types);
    }

    public HookInfo getByHook(String className, String methodName, Object... args) {
        return mHookMap.get(className, methodName, args);
    }

    public void removeByOrigin(Method origin) {
        removeByOrigin(origin.getDeclaringClass().getName(), origin.getName(), origin.getParameterTypes());
    }

    public void removeByOrigin(String className, String methodName, Class<?>... types) {
        HookInfo hookInfo = mOriginMap.remove(className, methodName, types);
        if (hookInfo != null) {
            mHookMap.remove(
                    hookInfo.getHook().getDeclaringClass().getName(),
                    hookInfo.getHook().getName(),
                    hookInfo.getHook().getTypeParameters());
        }
    }

    public void removeByHook(String className, String methodName, Class<?>... types) {
        HookInfo hookInfo = mHookMap.remove(className, methodName, types);
        if (hookInfo != null) {
            mOriginMap.remove(
                    hookInfo.getOrigin().getDeclaringClass().getName(),
                    hookInfo.getOrigin().getName(),
                    hookInfo.getOrigin().getTypeParameters());
        }
    }

    private class HookInfoMap {

        private Map<String, Map<String, List<HookInfo>>> nMap = new HashMap<>();

        public HookInfoMap() {

        }

        private void put(String className, String methodName, HookInfo hookInfo) {
            Map<String, List<HookInfo>> hookInfoMap = nMap.get(className);
            if (hookInfoMap == null) {
                hookInfoMap = new HashMap<>();
                nMap.put(className, hookInfoMap);
            }
            List<HookInfo> hookInfos = hookInfoMap.get(methodName);
            if (hookInfos == null) {
                hookInfos = new LinkedList<>();
                hookInfoMap.put(methodName, hookInfos);
            }
            hookInfos.add(hookInfo);
        }

        private <T> HookInfo get(String className, String methodName, T[] args) {
            Map<String, List<HookInfo>> hookInfoMap = nMap.get(className);
            if (hookInfoMap == null) {
                return null;
            }
            List<HookInfo> hookInfos = hookInfoMap.get(methodName);
            if (hookInfos == null) {
                return null;
            }
            for (HookInfo hookInfo : hookInfos) {
                if (ReflectUtil.equalParams(hookInfo.getParameterTypes(), args)) {
                    return hookInfo;
                }
            }
            return null;
        }

        private <T> HookInfo remove(String className, String methodName, T[] args) {
            Map<String, List<HookInfo>> hookInfoMap = nMap.get(className);
            if (hookInfoMap == null) {
                return null;
            }
            List<HookInfo> hookInfos = hookInfoMap.get(methodName);
            if (hookInfos == null) {
                return null;
            }
            for (Iterator<HookInfo> iterator = hookInfos.iterator(); iterator.hasNext();) {
                HookInfo hookInfo = iterator.next();
                if (ReflectUtil.equalParams(iterator.next().getParameterTypes(), args)) {
                    iterator.remove();
                    if (hookInfos.isEmpty()) {
                        hookInfoMap.remove(methodName);
                        if (hookInfoMap.isEmpty()) {
                            nMap.remove(className);
                        }
                    }
                    return hookInfo;
                }
            }
            return null;
        }
    }
}
