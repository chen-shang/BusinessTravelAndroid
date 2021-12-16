package com.business.travel.app.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

import androidx.annotation.Nullable;
import com.blankj.utilcode.util.FileIOUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.PathUtils;
import com.business.travel.app.exceptions.ApiException;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.utils.HttpWrapper;
import com.business.travel.utils.JacksonUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

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

	/**
	 * @param iconFullName
	 * @return
	 */
	@Nullable
	public static InputStream getIcon(String iconFullName) {
		if (StringUtils.isBlank(iconFullName)) {
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
		return new FileInputStream(file);
	}

	private static InputStream getIconFromServer(String iconFullName) {
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
