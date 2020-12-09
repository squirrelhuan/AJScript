package cn.demomaster.ajscript;


import android.os.Environment;

import cn.demomaster.huan.quickdeveloplibrary.QDApplication;
import cn.demomaster.huan.quickdeveloplibrary.helper.cache.QuickCache;
import cn.demomaster.qdlogger_library.QDLogger;

public class MyApp extends QDApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        QDLogger.init(this, "/ajscript/");
        //初始化缓存目录
        QuickCache.init(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/ajscript/cache/");

    }
}
