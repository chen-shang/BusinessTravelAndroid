package com.business.travel.app;

import android.app.Application;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;

public class BootApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		//开启日志记录写入文件,最大栈深度为5,最多保留一天 todo 生产环境关闭日志提高程序速度
		LogUtils.getConfig().setLog2FileSwitch(true).setStackDeep(5).setSaveDays(1).setLogSwitch(true);

		CrashUtils.init();
	}
}
