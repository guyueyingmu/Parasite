package com.alipay.mobile.quinox;

import android.content.Context;

import com.hu.parasite.Parasite;
import com.hu.parasite.annotation.TargetMethod;
import com.hu.parasite.hook.HookManager;
import com.hu.parasite.util.LogUtil;

/**
 * Created by HuJi on 2018/1/1.
 */

public class ParasiteMain extends Parasite {

    private static final String TAG = ParasiteMain.class.getSimpleName();

    @TargetMethod("com.alipay.mobile.common.logging.util.LoggingUtil##isDebuggable##android.content.Context")
    public static boolean isDebuggable(Context context) throws Exception {
        LogUtil.d(TAG, "isDebuggable: %s", HookManager.getInstance().invoke(null, context));
        return true;
    }
}
