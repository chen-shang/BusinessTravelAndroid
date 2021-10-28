package com.business.travel.app.constant;

import java.util.Collections;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class HttpClientConstant {
	/**
	 * 访问后台的http client
	 *
	 * 不用每次都新建一个
	 */
	public static final OkHttpClient BUSINESS_TRAVEL_HTTP_CLIENT = new OkHttpClient.Builder()
			.cookieJar(new CookieJar() {
				@Override
				public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> list) {
				}

				@NotNull
				@Override
				public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
					return Collections.emptyList();
				}
			}).build();
}
