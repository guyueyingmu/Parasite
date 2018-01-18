package com.hu.parasite.util;

import android.util.Log;

/**
 * Created by HuJi on 2017/12/29.
 */

public class LogUtil {

    private static boolean mDebug = false;

    public static void setDebug(boolean debug) {
        mDebug = debug;
    }

    public static void v(final String tag, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            android.util.Log.v(tag, log);
        }
    }

    public static void e(final String tag, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            android.util.Log.e(tag, log);
        }
    }

    public static void w(final String tag, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            android.util.Log.w(tag, log);
        }
    }

    public static void i(final String tag, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            android.util.Log.i(tag, log);
        }
    }

    public static void d(final String tag, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            android.util.Log.d(tag, log);
        }
    }

    public static void printErrStackTrace(String tag, Throwable tr, final String format, final Object... args) {
        if (mDebug) {
            String log = (args == null || args.length == 0) ? format : String.format(format, args);
            if (log == null) {
                log = "";
            }
            log += "  " + Log.getStackTraceString(tr);
            android.util.Log.e(tag, log);
        }
    }
}