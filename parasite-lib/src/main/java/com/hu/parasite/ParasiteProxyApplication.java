package com.hu.parasite;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.hu.parasite.util.FileUtil;
import com.hu.parasite.util.ReflectUtil;

import java.io.File;

/**
 * Created by HuJi on 2017/12/30.
 */

public class ParasiteProxyApplication extends ParasiteApplication {

    @Override
    public void onInit(Context context) throws Throwable
    {
        // Todo: 路径写死了，到时候要改
        File app = new File("/data/local/tmp/alipay_wap_main.apk");

        String rootDir = FileUtil.concat(getFilesDir().getAbsolutePath(), FileUtil.hashCode(app));
        String fileDir = FileUtil.concat(rootDir,  "datas");
;
        // 支持的abi
        String[] abis = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
        }

        // 拷贝文件、解压文件
        File appDir = new File(FileUtil.concat(rootDir, "parastisu.apk"));
        if (!appDir.exists()) {
            Toast.makeText(this, "wait a moment! copy file...", Toast.LENGTH_LONG).show();
            FileUtil.copyFile(app, appDir);
            FileUtil.unzip(appDir, fileDir);
        }
        setAppDir(appDir.getAbsolutePath());

        // dex文件优化目录
        File codeDir = new File(FileUtil.concat(rootDir, "cache-code"));
        codeDir.mkdirs();
        setCodeDir(codeDir.getAbsolutePath());

        // 找到对应的abi
        for (String abi : abis) {
            if (!TextUtils.isEmpty(abi)) {
                File libDir = new File(FileUtil.concat(fileDir, "lib" + File.separator + abi));
                if (libDir.exists()) {
                    String path = libDir.getAbsolutePath();
                    String[] cmd = new String[] { "chmod", "-R", "775", path };
                    Runtime.getRuntime().exec(cmd);
                    setLibraryDir(path);
                    break;
                }
            }
        }

        // 资源路径
        for (String res : new String[]{"res", "r"}) {
            File resDir = new File(FileUtil.concat(fileDir, res));
            if (resDir.exists()) {
                setResourceDir(resDir.getAbsolutePath());
                break;
            }
        }

        // Todo: 写死了Application
        setAppClassName(getClass().getName());
    }

    @Override
    public void onLoad(ClassLoader classLoader) throws Throwable {
        // Todo: 名字写死了，到时候要改
        Parasite parasite = (Parasite) ReflectUtil.newInstance("com.alipay.mobile.quinox.ParasiteMain");
        parasite.invoke();
    }

    @Override
    public void onError(Throwable e) {
        if (TextUtils.isEmpty(getAppDir())) {
            new File(getAppDir()).deleteOnExit();
        }
        // Todo: 自杀
    }
}
