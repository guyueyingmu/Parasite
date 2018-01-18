package com.hu.parasite.hook;

import com.hu.parasite.util.Util;

/**
 * Created by HuJi on 2018/1/14.
 */

public class HookException extends Exception {

    public HookException(String format, Object... args) {
        super(Util.isEmpty(args) ? format : String.format(format, args));
    }
}
