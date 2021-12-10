package com.business.travel.app.api;

import androidx.annotation.Nullable;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.exceptions.ApiException;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.utils.HttpWrapper;
import com.business.travel.utils.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Sets;
import okhttp3.*;
import okhttp3.Request.Builder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author chenshang
 */
public class BusinessTravelResourceApi {

	private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(new OkHttpClient());
	private static final String OWNER = "chen-shang";
	private static final String REPOSITORY = "business-travel-resource";
	private static final String BASE_URL = "https://gitee.com/api/v5/repos";
	private static final String ACCESS_TOKEN = "a1b50339ccf80a7c96f6a96fa97fcdaf";
	private static final String URL_ = BASE_URL + "/" + OWNER + "/" + REPOSITORY;

	private static final Set<String> set = Sets.newConcurrentHashSet();

	/**
	 * @param iconFullName
	 * @return
	 */
	@Nullable
	public static InputStream getIcon(String iconFullName) {
		if (StringUtils.isBlank(iconFullName)) {
			return null;
		}

		//TODO 查询缓存,缓存不存在,从服务端查询
		InputStream inputStream = getIconFromServer(iconFullName);
		if (inputStream != null) {
			//todo 保存并添加缓存
		}
		return inputStream;
	}

	private static InputStream getIconFromServer(String iconFullName) {
		String md5 = DigestUtils.md5DigestAsHex(iconFullName.getBytes());
		if (!set.add(md5)) {
			return null;
		}
		try {
			Request request = new Builder().url(iconFullName).build();
			Response response = new OkHttpClient().newCall(request).execute();
			if (!response.isSuccessful()) {
				throw new RuntimeException("请求失败:" + response.code() + ",请稍后再试");
			}
			ResponseBody body = response.body();
			if (body == null) {
				throw new RuntimeException("请求失败,请稍后再试");
			}
			return body.byteStream();
		} catch (IOException e) {
			throw new IllegalArgumentException("获取图标失败");
		} finally {
			set.remove(md5);
		}
	}

	/**
	 * 获取仓库具体路径下的内容
	 * https://gitee.com/api/v5/swagger#/getV5ReposOwnerRepoContents(Path)
	 */
	public static List<GiteeContent> getV5ReposOwnerRepoContents(String path) {
		String uri = URL_ + "/contents/" + path;
		HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder().addQueryParameter("access_token", ACCESS_TOKEN);
		Request request = new Builder().url(urlBuilder.build()).build();
		try {
			String response = httpClient.sendRequest(request);
			LogUtils.i(new StringJoiner("\n")
					.add("获取仓库具体路径下的内容")
					.add("uri:" + uri)
					.add("response:" + response)
					.toString()
			);
			if (StringUtils.isEmpty(response)) {
				return Collections.emptyList();
			}
			return JacksonUtil.toBean(response, new TypeReference<List<GiteeContent>>() {
			});
		} catch (IOException e) {
			throw new ApiException(-1, "网络异常,请稍后重试");
		}
	}
}
