package com.business.travel.app.utils;

import java.io.IOException;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.business.travel.utils.JacksonUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author chenshang
 */
@RequiresApi(api = VERSION_CODES.N)
public class HttpWrapper {
	private final OkHttpClient httpClient;

	private HttpWrapper(OkHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public static HttpWrapper withOkHttpClient(OkHttpClient httpClient) {
		return new HttpWrapper(httpClient);
	}

	/**
	 * doGet
	 *
	 * @param url http请求的url路径
	 * @return 默认doGet返回String
	 * @throws IOException
	 */
	public String doGet(String url) throws IOException {
		return doGet(url, String.class);
	}

	/**
	 * doPost
	 *
	 * @param url http请求的url路径
	 * @param req 请求参数
	 * @return 默认doGet返回String
	 * @throws IOException
	 */
	public String doPost(String url, Object req) throws IOException {
		return doPost(url, req, String.class);
	}

	/**
	 * 通用的post请求
	 *
	 * @param url   http请求的url路径
	 * @param req   请求参数
	 * @param clazz 返回参数类型
	 * @param <Req> 请求参数类型
	 * @param <Res> 返回参数类型
	 * @return 返回结果
	 * @throws IOException
	 */
	public <Req, Res> Res doPost(String url, Req req, Class<Res> clazz) throws IOException {
		String content = JacksonUtil.toString(req);
		Request request = new Builder()
				.url(url)
				.post(RequestBody.create(MediaType.parse("application/json"), content))
				.build();
		String responseBody = sendRequest(request);
		if (String.class.equals(clazz)) {
			return (Res)responseBody;
		}
		return JacksonUtil.toBean(responseBody, clazz);
	}

	/**
	 * @param url
	 * @param clazz
	 * @param <Res>
	 * @return
	 * @throws IOException
	 */
	public <Res> Res doGet(String url, Class<Res> clazz) throws IOException {
		Request request = new Builder()
				.url(url)
				.build();
		String responseBody = sendRequest(request);
		if (String.class.equals(clazz)) {
			return (Res)responseBody;
		}

		return JacksonUtil.toBean(responseBody, clazz);
	}

	/**
	 * @param request
	 * @return
	 * @throws IOException
	 */
	private String sendRequest(Request request) throws IOException {
		try (Response response = httpClient.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				throw new RuntimeException("请求失败:" + response.code() + ",请稍后再试");
			}
			ResponseBody body = response.body();
			if (body == null) {
				return "";
			}
			return body.string();
		}
	}
}
