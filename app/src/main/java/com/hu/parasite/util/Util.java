package com.hu.parasite.util;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by HuJi on 2018/1/14.
 */

public class Util {

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static int readBytes(InputStream is, byte[] b, int len)
            throws IOException {
        return readBytes(is, b, len, false);
    }

    public static int readBytes(InputStream is, byte[] b, int len, boolean wait)
            throws IOException {
        int off = 0;
        while (off < len) {
            int size = is.read(b, off, len);
            if (size < 0 || (!wait && size == 0)) {
                break;
            }
            off += size;
        }
        return off;
    }

    public static int readBytes(InputStream is, byte[] b)
            throws IOException {
        return readBytes(is, b, b.length, false);
    }

    public static int readBytes(InputStream is, byte[] b, boolean wait)
            throws IOException {
        return readBytes(is, b, b.length, wait);
    }

    public static byte[] readBytes(InputStream is, int len)
            throws IOException {
        byte[] b = new byte[len];
        readBytes(is, b, len, true);
        return b;
    }
}
