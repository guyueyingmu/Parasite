package com.alipay.mobile.quinox;

import com.hu.parasite.Parasite;
import com.hu.parasite.ParasiteProxyApplication;
import com.hu.parasite.hook.HookManager;
import com.hu.parasite.util.LogUtil;
import com.hu.parasite.util.ReflectUtil;

/**
 * Created by HuJi on 2018/1/1.
 */

public class ParasiteMain extends Parasite {

    public void invoke() throws Exception {
        super.invoke();
        try {
            getHookManager().hook(
                    ReflectUtil.getMethod(ParasiteProxyApplication.class, "onLoad", ClassLoader.class),
                    ReflectUtil.getMethod(ParasiteMain.class, "onLoad", Object.class, ClassLoader.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onLoad(Object aaa, ClassLoader classLoader) throws Exception {
        try {
            LogUtil.d("dsadsadsadsa","ParasiteMain_onInitializeSuccess");
            getHookManager().invoke(aaa, classLoader);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
