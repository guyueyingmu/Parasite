package com.hu.parasite;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.mobile.quinox.ParasiteMain;
import com.hu.parasite.util.FileUtil;
import com.hu.parasite.util.LogUtil;

import java.io.File;

/**
 * Created by HuJi on 2017/12/30.
 */

public class ParasiteProxyApplication extends ParasiteApplication {

    @Override
    public void onInit(Context context) throws Exception
    {
        File app = new File("/data/local/tmp/alipay_wap_main.apk");

        String rootDir = FileUtil.concat(getFilesDir().getAbsolutePath(), FileUtil.hashCode(app));
        String fileDir = FileUtil.concat(rootDir,  "datas");
;
        File appDir = new File(FileUtil.concat(rootDir, "parastisu.apk"));
        if (!appDir.exists()) {
            Toast.makeText(this, "wait a moment! copy file...", Toast.LENGTH_LONG).show();
            FileUtil.copyFile(app, appDir);
            FileUtil.unzip(appDir, fileDir);
        }
        setAppDir(appDir.getAbsolutePath());

        String[] abis = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            abis = Build.SUPPORTED_ABIS;
        } else {
            abis = new String[] { Build.CPU_ABI, Build.CPU_ABI2 };
        }
        for (String abi : abis) {
            if (!TextUtils.isEmpty(abi)) {
                File libDir = new File(FileUtil.concat(fileDir, "lib" + File.separator + abi));
                if (libDir.exists()) {
                    setLibraryDir(libDir.getAbsolutePath());
                    break;
                }
            }
        }

        setResourceDir(null);
        setAppClassName(getClass().getName());

        Parasite parasite = new ParasiteMain();
        parasite.invoke();
    }

    @Override
    public void onLoad(ClassLoader classLoader) throws Exception {
        LogUtil.d("ParasiteProxyApplication", "ParasiteProxyApplication_onInitializeSuccess");
    }

    @Override
    public void onError(Exception e) {
        // Todo: 自杀
    }
}
