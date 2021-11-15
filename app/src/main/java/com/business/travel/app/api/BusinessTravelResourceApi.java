package com.business.travel.app.api;

import java.io.IOException;
import java.io.InputStream;

import com.business.travel.app.utils.HttpWrapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;

/**
 * @author chenshang
 */
public class BusinessTravelResourceApi {

	private static final String URL_ = "https://gitee.com/chen-shang/business-travel-resource/raw/master/icon";
	private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(new OkHttpClient());

	public static InputStream getIcon(String iconPath, String iconName) {
		try {
			//TODO 缓存
			Request request = new Builder().url(URL_ + "/" + iconPath + "/" + iconName).build();
			Response response = new OkHttpClient().newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("请求失败:" + response.code() + ",请稍后再试");
			}
			final InputStream inputStream = response.body().byteStream();
			return inputStream;
		} catch (IOException e) {
			throw new IllegalArgumentException("获取图标失败");
		}
	}
}
