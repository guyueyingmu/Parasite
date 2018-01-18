package com.hu.parasite.preset;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.hu.parasite.ParasiteApplication;
import com.hu.parasite.hook.HookManager;
import com.hu.parasite.util.ReflectUtil;

/**
 * Created by HuJi on 2018/1/17.
 */

public class PresetPackageManager implements IPreset {

    @Override
    public void invoke() throws Exception {
        HookManager.getsInstance().hook(
                ReflectUtil.getMethod("android.content.pm.IPackageManager$Stub$Proxy", "getApplicationInfo", String.class, int.class, int.class),
                ReflectUtil.getMethod(PresetPackageManager.class, "getApplicationInfo", Object.class, String.class, int.class, int.class));
    }

    private static ApplicationInfo sApplicationInfo = ParasiteApplication.getInstance().getApplicationInfo();

    private static ApplicationInfo getApplicationInfo(Object pm, String packageName, int flags ,int userId) throws Exception {
        if (packageName.equals(sApplicationInfo.packageName)) {
            return sApplicationInfo;
        }
        return HookManager.getsInstance().invoke(pm, packageName, flags, userId);
    }
}