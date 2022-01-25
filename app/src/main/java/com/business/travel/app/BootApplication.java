package com.business.travel.app;

import android.app.Application;
import com.blankj.utilcode.util.CrashUtils;
import com.business.travel.app.utils.LogToast;

/**
 * app启动的时候执行,也是整个项目的入口
 * 适合进行全局的配置初始化,注意这个地方还没有给用户展示的页面
 */
public class BootApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtils.init(crashInfo -> LogToast.errorShow(crashInfo.toString()));
    }
}
