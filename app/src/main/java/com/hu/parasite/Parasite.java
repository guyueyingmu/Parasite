package com.hu.parasite;

import com.hu.parasite.hook.HookManager;
import com.hu.parasite.preset.IPreset;
import com.hu.parasite.preset.PresetPackageManager;

/**
 * Created by HuJi on 2018/1/2.
 */

public abstract class Parasite {

    private static IPreset[] mPresets = new IPreset[] {
            new PresetPackageManager()
    };

    public static HookManager getHookManager() {
        return HookManager.getsInstance();
    }

    public void invoke() throws Exception {
        for (IPreset preset : mPresets) {
            preset.invoke();
        }
    }
}
