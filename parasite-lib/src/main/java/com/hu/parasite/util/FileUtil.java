package com.hu.parasite.util;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by HuJi on 2018/1/16.
 */

public class FileUtil {

    private static final String TAG = FileUtil.class.getSimpleName();
    private static final int BUFFER_SIZE = 4096;
    private static final int SMALL_BUFFER_SIZE = 512;

    public static void unzip(File zip, String dest) throws IOException {
        byte buffer[] = new byte[BUFFER_SIZE];
        int length = -1;
        FileInputStream fis = null;
        ZipInputStream zis = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            fis = new FileInputStream(zip);
            zis = new ZipInputStream(fis);
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    File file = new File(concat(dest, entry.getName()));
                    file.getParentFile().mkdirs();
                    fos = new FileOutputStream(file);
                    bos = new BufferedOutputStream(fos);
                    while ((length = zis.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        bos.write(buffer, 0, length);
                    }
                    zis.closeEntry();
                    bos = close(bos);
                    fos = close(fos);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            close(bos);
            close(fos);
            close(zis);
        }
    }

    public static String hashCode(File file) throws IOException {
        return hashCode(new FileInputStream(file), file.length());
    }

    public static String hashCode(InputStream is, long size) throws IOException {
        byte[] buffer = new byte[SMALL_BUFFER_SIZE];
        int length = -1;
        StringBuilder hash = new StringBuilder(String.format("FILE_%x", size));
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (size > SMALL_BUFFER_SIZE) {
                is.skip(size - SMALL_BUFFER_SIZE);
            }
            if ((length = is.read(buffer, 0, SMALL_BUFFER_SIZE)) != -1) {
                md.update(buffer, 0, length);
            }
            BigInteger bi = new BigInteger(1, md.digest());
            hash.append(bi.toString(16));
        } catch (NoSuchAlgorithmException e) {
            LogUtil.printErrStackTrace(TAG, e, "hashCode");
        }
        return hash.toString();
    }

    public static void copyFile(File src, File dest) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        try {
            dest.getParentFile().mkdirs();
            in = new FileInputStream(src).getChannel();
            out = new FileOutputStream(dest).getChannel();
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            throw e;
        } finally {
            close(in);
            close(out);
        }
    }

    public static void copyFile(FileDescriptor src, File dest) throws IOException {
        FileChannel in = null;
        FileChannel out = null;
        try {
            dest.getParentFile().mkdirs();
            in = new FileInputStream(src).getChannel();
            out = new FileOutputStream(dest).getChannel();
            in.transferTo(0, in.size(), out);
        } catch (Exception e) {
            throw e;
        } finally {
            close(in);
            close(out);
        }
    }

    public static String concat(String dir, String file) {
//        if (dir.endsWith(File.separator) || file.startsWith(File.separator)) {
//            return dir + file;
//        }
        return dir + File.separator + file;
    }

    private static <T extends Closeable> T close(T closeable) throws IOException {
        if (closeable != null) {
            closeable.close();
        }
        return null;
    }
}
