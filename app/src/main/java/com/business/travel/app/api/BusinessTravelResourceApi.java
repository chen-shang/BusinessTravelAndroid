package com.business.travel.app.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import androidx.annotation.Nullable;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.PathUtils;
import com.business.travel.app.constant.AppConfig;
import com.business.travel.app.exceptions.ApiException;
import com.business.travel.app.model.Config;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.utils.HttpWrapper;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.DigestUtils;

/**
 * @author chenshang
 *
 * 远程资源API
 */
public class BusinessTravelResourceApi {

	private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(new OkHttpClient());
	private static final String ACCESS_TOKEN = "a1b50339ccf80a7c96f6a96fa97fcdaf";

	/**
	 * @param iconFullName
	 * @return
	 */
	@Nullable
	public static InputStream getIcon(String iconFullName) {
		if (StringUtils.isBlank(iconFullName)) {
			return null;
		}

		if (!NetworkUtils.isAvailable()) {
			return null;
		}

		try {
			InputStream inputStream = getIconFromCache(iconFullName);
			if (inputStream != null) {
				return inputStream;
			}

			inputStream = getIconFromServer(iconFullName);
			return addIconToCache(iconFullName, inputStream);
		} catch (Exception e) {
			LogUtils.e("获取图标文件失败,请稍后重试:" + e.getMessage());
		}
		return null;
	}

	private static InputStream addIconToCache(String iconFullName, InputStream inputStream) throws FileNotFoundException {
		String md5 = DigestUtils.md5DigestAsHex(iconFullName.getBytes());
		File file = new File(PathUtils.getExternalAppFilesPath(), md5 + ".svg");
		FileIOUtils.writeFileFromIS(file, inputStream, false);
		return new FileInputStream(file);
	}

	private static InputStream getIconFromCache(String iconFullName) throws FileNotFoundException {
		String md5 = DigestUtils.md5DigestAsHex(iconFullName.getBytes());
		File file = new File(PathUtils.getExternalAppFilesPath(), md5 + ".svg");
		if (!file.exists()) {
			return null;
		}

		//超过3分钟重新下载
		long nowTimestamp = DateTimeUtil.timestamp();
		long lastModified = file.lastModified();
		Long iconTtl = Optional.ofNullable(AppConfig.getConfig()).map(Config::getIconTtl).orElse(5 * 60 * 1000L);
		if (nowTimestamp - lastModified > iconTtl) {
			boolean delete = file.delete();
			LogUtils.i("缓存超时失效" + iconFullName + " :" + delete);
			return null;
		}

		return new FileInputStream(file);
	}

	private static InputStream getIconFromServer(String iconFullName) {
		try {
			ResponseBody body = getResponseBody(iconFullName);
			return body.byteStream();
		} catch (IOException e) {
			throw new IllegalArgumentException("获取图标失败");
		}
	}

	/**
	 * 获取仓库具体路径下的内容
	 * https://gitee.com/api/v5/swagger#/getV5ReposOwnerRepoContents(Path)
	 */
	public static List<GiteeContent> getRepoContents(String path) {
		String uri = "https://gitee.com/api/v5/repos/chen-shang/business-travel-resource/contents/" + path;
		HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(uri)).newBuilder().addQueryParameter("access_token", ACCESS_TOKEN);
		Request request = new Builder().url(urlBuilder.build()).build();
		try {
			String response = httpClient.sendRequest(request);
			LogUtils.i(new StringJoiner("\n").add("获取仓库具体路径下的内容").add("uri:" + uri).add("response:" + response).toString());
			if (StringUtils.isEmpty(response)) {
				return Collections.emptyList();
			}
			return JacksonUtil.toBean(response, new TypeReference<List<GiteeContent>>() {
			});
		} catch (IOException e) {
			throw new ApiException(-1, "网络异常,请稍后重试");
		}
	}

	public static String getRepoRaw(String path) {
		try {
			String uri = "https://gitee.com/chen-shang/business-travel-resource/raw/" + path;
			ResponseBody body = getResponseBody(uri);
			return body.string();
		} catch (IOException e) {
			throw new IllegalArgumentException("获取文件失败");
		}
	}

	@NotNull
	private static ResponseBody getResponseBody(String uri) throws IOException {
		Request request = new Builder().url(uri).build();
		Response response = new OkHttpClient().newCall(request).execute();
		if (!response.isSuccessful()) {
			throw new RuntimeException("请求失败:" + response.code() + ",请稍后再试");
		}
		ResponseBody body = response.body();
		if (body == null) {
			throw new RuntimeException("请求失败,请稍后再试");
		}
		return body;
	}
}
