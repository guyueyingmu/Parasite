package com.hu.parasite;

import com.hu.parasite.hook.HookManager;
import com.hu.parasite.hook.Hookable;
import com.hu.parasite.proxy.ActivityManagerProxy;
import com.hu.parasite.proxy.PackageManagerProxy;

/**
 * Created by HuJi on 2018/1/2.
 */

public abstract class Parasite {

    private static final Hookable[] HOOKABLES = new Hookable[] {
            new PackageManagerProxy(),
            new ActivityManagerProxy(),
    };

    public void invoke() throws Throwable {
        for (Hookable hookable : HOOKABLES) {
            hookable.hook();
        }
        HookManager.getInstance().hook(getClass());
    }
}
