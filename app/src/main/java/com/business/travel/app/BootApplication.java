package com.business.travel.app;

import android.app.Application;
import com.blankj.utilcode.util.CrashUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.constant.AppConfig;
import com.business.travel.app.constant.BusinessTravelResourceConstant;
import com.business.travel.app.model.Config;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.NetworkUtil;
import com.business.travel.utils.JacksonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * app启动的时候执行,也是整个项目的入口
 * 适合进行全局的配置初始化
 */
public class BootApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CrashUtils.init(crashInfo -> LogToast.errorShow(crashInfo.toString()));

        //启动的时候初始化配置
        FutureUtil.supplyAsync(() -> {
            if (!NetworkUtil.isAvailable()) {
                return "{}";
            }
            return BusinessTravelResourceApi.getRepoRaw(BusinessTravelResourceConstant.APP_CONFIG_PATH);
        }).whenComplete((config, throwable) -> {
            if (throwable != null) {
                LogUtils.e(throwable.getMessage());
                LogToast.errorShow(throwable.getMessage());
                return;
            }
            if (StringUtils.isBlank(config)) {
                return;
            }
            Config newConfig = JacksonUtil.toBean(config, Config.class);
            AppConfig.setConfig(newConfig);

            //开启日志记录写入文件,最大栈深度为1,最多保留一天
            Boolean logSwitch = Optional.of(newConfig.getLogSwitch()).orElse(true);
            LogUtils.getConfig().setLog2FileSwitch(true).setSaveDays(1).setLogSwitch(logSwitch);

            LogUtils.i("加载配置文件:" + config);
        });
    }
}
