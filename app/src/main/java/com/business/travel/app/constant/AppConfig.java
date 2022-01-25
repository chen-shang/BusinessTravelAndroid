package com.business.travel.app.constant;

import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.model.Config;
import com.business.travel.app.utils.NetworkUtil;
import com.business.travel.utils.JacksonUtil;
import lombok.Getter;

/**
 * app全局的配置都在这里设置
 */
public final class AppConfig {
    @Getter
    private static Config config;

    public static void refreshConfig() {
        if (!NetworkUtil.isAvailable() && config == null) {
            config = new Config();
            return;
        }

        String newConfig = BusinessTravelResourceApi.getRepoRaw(BusinessTravelResourceConstant.APP_CONFIG_PATH);
        LogUtils.i("从远程加载配置文件:" + newConfig);
        config = JacksonUtil.toBean(newConfig, Config.class);
    }
}
