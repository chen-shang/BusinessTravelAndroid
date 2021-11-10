package com.business.travel.app.api;

import java.io.File;

import com.business.travel.app.utils.HttpWrapper;
import okhttp3.OkHttpClient;

/**
 * @author chenshang
 */
public class BusinessTravelResourceApi {

    private static final String URL_ = "https://gitee.com/chen-shang/business-travel-resource/raw/master/icon/";
    private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(new OkHttpClient());

    public File getIcon(String iconName) {
        return null;
    }
}
