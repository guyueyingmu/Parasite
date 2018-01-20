package com.hu.parasite;

import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.util.ArrayMap;

import com.hu.parasite.util.LogUtil;
import com.hu.parasite.util.ReflectUtil;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by HuJi on 2017/12/29.
 */

public abstract class ParasiteApplication extends Application {

    public static final String TAG = ParasiteApplication.class.getName();
    private static volatile ParasiteApplication sInstance = null;

    private String mAppDir = null;
    private String mCodeDir = null;
    private String mLibraryDir = null;
    private String mResourceDir = null;
    private String mAppClassName = null;

    private Application mApplication = null;
    private ParasiteClassLoader mClassLoader = null;

    public static ParasiteApplication getInstance() {
        return sInstance;
    }

    protected ParasiteApplication() {
        if (sInstance != null) {
            throw new RuntimeException("ProxyApplication has been instantiated");
        } else {
            sInstance = this;
            LogUtil.setDebug(true);
        }
    }

    /**
     * 做一些初始化的活(需要设置mParasitifer)
     */
    public abstract void onInit(Context context) throws Throwable;

    /**
     * 加载成功，可以干一些事了
     */
    public abstract void onLoad(ClassLoader classLoader) throws Throwable;

    /**
     * 加载失败，自杀吧
     */
    public abstract void onError(Throwable e);

    /**
     * apk路径
     */
    public String getAppDir() {
        return mAppDir;
    }

    /**
     * apk路径
     */
    protected void setAppDir(String appDir) {
        mAppDir = appDir;
    }

    /**
     * dex优化路径
     */
    public String getCodeDir() {
        return mCodeDir;
    }

    /**
     * dex优化路径
     */
    protected void setCodeDir(String codeDir) {
        this.mCodeDir = codeDir;
    }

    /**
     * lib路径
     */
    public String getLibraryDir() {
        return mLibraryDir;
    }

    /**
     * lib路径
     */
    protected void setLibraryDir(String libraryDir) {
        mLibraryDir = libraryDir;
    }

    /**
     * 资源路径
     */
    public String getResourceDir() {
        return mResourceDir;
    }

    /**
     * 资源路径
     */
    protected void setResourceDir(String resourceDir) {
        mResourceDir = resourceDir;
    }

    /**
     * Application名
     */
    public String getAppClassName() {
        return mAppClassName;
    }

    /**
     * Application名
     */
    protected void setAppClassName(String appClassName) {
        mAppClassName = appClassName;
    }

    /**
     * 加载指定类
     * @param name 类名
     * @return 类对象
     * @throws ClassNotFoundException
     */
    public Class loadClass(String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    /**
     * 加载指定类
     * @param name 类名
     * @param parent 填true则从parent ClassLoader找,否则从新ClassLoader找
     * @return 类对象
     * @throws ClassNotFoundException
     */
    public Class loadClass(String name, boolean parent) throws ClassNotFoundException {
        return parent ? mClassLoader.loadClass(name) : mClassLoader.findClass(name);
    }

    /**
     * 替换现在的ClassLoader
     * @param appDir
     * @param libraryDir
     * @param parent
     * @throws ReflectiveOperationException
     */
    private ParasiteClassLoader createClassLoader(String appDir, String codeDir, String libraryDir, ClassLoader parent)
            throws ReflectiveOperationException
    {
        ParasiteClassLoader classLoader = new ParasiteClassLoader(appDir, libraryDir, parent);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            DexClassLoader dexClassLoader = new DexClassLoader(appDir, codeDir, null, parent);
            Object pathList = ReflectUtil.get(dexClassLoader, "pathList");
            Object dexElements = ReflectUtil.get(pathList, "dexElements");
            pathList = ReflectUtil.get(classLoader, "pathList");
            ReflectUtil.set(pathList, "dexElements", dexElements);
        }

        Thread.currentThread().setContextClassLoader(classLoader);
        Object activityThread = ReflectUtil.invoke("android.app.ActivityThread", "currentActivityThread");
        Object mPackages = ReflectUtil.get(activityThread, "mPackages");
        for (Map.Entry<String, WeakReference<?>> entry
                : ((Map<String, WeakReference<?>>) mPackages).entrySet()) {
            Object loadedApk = entry.getValue().get();
            if (loadedApk != null) {
                ReflectUtil.set(loadedApk, "mClassLoader", classLoader);
            }
        }

        return classLoader;
    }

    /**
     * 替换现在的application
     * @param appClassName application类名
     * @return 新的application
     * @throws ReflectiveOperationException
     */
    private Application createApplication(String appClassName) throws ReflectiveOperationException
    {
        Object activityThread = ReflectUtil.invoke("android.app.ActivityThread", "currentActivityThread");

        // 将LoadApk的mApplication置空
        Object mBoundApplication = ReflectUtil.get(activityThread, "mBoundApplication");
        Object info = ReflectUtil.get(mBoundApplication, "info");
        ReflectUtil.set(info, "mApplication", null);

        // 移除mAllApplications中的mInitialApplication
        Object mInitialApplication = ReflectUtil.get(activityThread, "mInitialApplication");
        ArrayList<Application> mAllApplications =
                (ArrayList<Application>) ReflectUtil.get(activityThread, "mAllApplications");
        mAllApplications.remove(mInitialApplication);

        // 设置className
        ApplicationInfo mApplicationInfo = (ApplicationInfo) ReflectUtil.get(info, "mApplicationInfo");
        mApplicationInfo.className = appClassName;

        // 设置className
        ApplicationInfo appInfo = (ApplicationInfo) ReflectUtil.get(mBoundApplication, "appInfo");
        appInfo.className = appClassName;

        // 生成新的application
        Application application =
                (Application) ReflectUtil.invoke(info, "makeApplication",
                        boolean.class, Instrumentation.class, false, null);

        // 替换所有mApplication
        ReflectUtil.set(activityThread, "mInitialApplication", application);

        // 替换所有mContext
        Map mProviderMap = (Map) ReflectUtil.get(activityThread, "mProviderMap");
        for (Object providerClientRecord : mProviderMap.values()) {
            Object mLocalProvider = ReflectUtil.get(providerClientRecord, "mLocalProvider");
            ReflectUtil.set(mLocalProvider, "mContext", application);
        }

        return application;
    }

    /**
     * 替换app路径
     * @param appDir
     * @throws ReflectiveOperationException
     */
    private void replaceAppDir(Context context, String appDir) throws ReflectiveOperationException, PackageManager.NameNotFoundException {
        Object activityThread = ReflectUtil.invoke("android.app.ActivityThread", "currentActivityThread");

        Field packagesFiled = ReflectUtil.getField(activityThread, "mPackages");
        Field resourcePackagesFiled = ReflectUtil.getField(activityThread, "mResourcePackages");
        for (Field field : new Field[]{ packagesFiled, resourcePackagesFiled }) {
            Object value = field.get(activityThread);
            for (Map.Entry<String, WeakReference<?>> entry
                    : ((Map<String, WeakReference<?>>) value).entrySet()) {
                Object loadedApk = entry.getValue().get();
                if (loadedApk != null) {
                    ReflectUtil.set(loadedApk, "mAppDir", appDir);
                    ReflectUtil.set(loadedApk, "mResDir", appDir);
                }
            }
        }

        // 将LoadApk的mApplication置空
        Object mBoundApplication = ReflectUtil.get(activityThread, "mBoundApplication");
        Object info = ReflectUtil.get(mBoundApplication, "info");
        ReflectUtil.set(info, "mApplication", null);

        ApplicationInfo mApplicationInfo = (ApplicationInfo) ReflectUtil.get(info, "mApplicationInfo");
        mApplicationInfo.sourceDir = appDir;
        mApplicationInfo.publicSourceDir = appDir;

        ApplicationInfo appInfo = (ApplicationInfo) ReflectUtil.get(mBoundApplication, "appInfo");
        appInfo.sourceDir = appDir;
        appInfo.publicSourceDir = appDir;

        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        applicationInfo.sourceDir = appDir;
        applicationInfo.publicSourceDir = appDir;
    }

    /**
     * 替换资源路径（直接添加就好了）
     * @param context
     * @param resourcedir
     * @throws ReflectiveOperationException
     */
    private static void replaceResourceDir(Context context, String resourcedir)
            throws ReflectiveOperationException
    {
        Object activityThread = ReflectUtil.invoke("android.app.ActivityThread", "currentActivityThread");

        // 参考的Tinker资源修复部分
        Collection<WeakReference<Resources>> references;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Object resourcesManager = ReflectUtil.invoke(
                    "android.app.ResourcesManager", "getInstance");
            try {
                Object activeResources19 = ReflectUtil.get(resourcesManager, "mActiveResources");
                references = ((ArrayMap<?, WeakReference<Resources>>)activeResources19).values();
            } catch (NoSuchFieldException e) {
                references = (Collection<WeakReference<Resources>>)
                        ReflectUtil.get(resourcesManager, "mResourceReferences");
            }
        } else {
            Object activeResources7 = ReflectUtil.get(activityThread, "mActiveResources");
            references = ((HashMap<?, WeakReference<Resources>>)activeResources7).values();
        }

        for (WeakReference<Resources> wr : references) {
            Resources resources = wr.get();
            if (resources != null) {
                Object assetManager = null;
                try {
                    assetManager = ReflectUtil.get(resources, "mAssets");
                } catch (Throwable e) {
                    Object resourceImpl = ReflectUtil.get(references, "mResourcesImpl");
                    assetManager = ReflectUtil.get(resourceImpl, "mAssets");
                }
                ReflectUtil.invoke(assetManager, "addAssetPath", String.class, resourcedir);
                ReflectUtil.invoke(assetManager, "ensureStringBlocks");
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        LogUtil.d(TAG, "attachBaseContext");
        super.attachBaseContext(base);
        try {
            onInit(base);
            mClassLoader = createClassLoader(getAppDir(), getCodeDir(), getLibraryDir(), base.getClassLoader());
            if (!TextUtils.isEmpty(getAppDir())) {
                replaceAppDir(base, getAppDir());
            }
            if (!TextUtils.isEmpty(getResourceDir())) {
                replaceResourceDir(base, getResourceDir());
            }
            onLoad(mClassLoader);
        } catch (Throwable e) {
            onError(e);
            LogUtil.printErrStackTrace(TAG, e, "attachBaseContext");
        }
    }

    @Override
    public void onCreate() {
        LogUtil.d(TAG, "onCreate");
        super.onCreate();
        try {
            if (!TextUtils.isEmpty(getAppClassName())) {
                mApplication = createApplication(getAppClassName());
            }
        } catch (Throwable e) {
            onError(e);
            LogUtil.printErrStackTrace(TAG, e, "onCreate");
        }
        if (mApplication != null) {
            mApplication.onCreate();
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        if (mApplication != null) {
            return mApplication.getClassLoader();
        }
        return super.getClassLoader();
    }

    private class ParasiteClassLoader extends PathClassLoader {

        public ParasiteClassLoader(String dexPath, String librarySearchPath, ClassLoader parent) {
            super(dexPath, librarySearchPath, parent);
        }

        @Override
        protected Class<?> findClass(String name) throws ClassNotFoundException {
            return super.findClass(name);
        }

        /**
         * fuck双亲委派
         * @param name
         * @return
         * @throws ClassNotFoundException
         */
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            if (name.equals(getAppClassName())) {
                return super.findClass(name);
            }
            return super.loadClass(name);
        }
    }
}