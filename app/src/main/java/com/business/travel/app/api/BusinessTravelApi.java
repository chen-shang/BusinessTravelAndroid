package com.business.travel.app.api;

import java.io.IOException;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import com.business.travel.app.aop.Async;
import com.business.travel.app.constant.HttpClientConstant;
import com.business.travel.app.utils.HttpWrapper;
import com.business.travel.vo.request.account.AccountRegisterRequest;

/**
 * @author chenshang
 */
@RequiresApi(api = VERSION_CODES.N)
public class BusinessTravelApi {

	private static final String URL_ = "http://30.240.83.105:8080/travel";
	private static final HttpWrapper httpClient = HttpWrapper.withOkHttpClient(HttpClientConstant.BUSINESS_TRAVEL_HTTP_CLIENT);

	@Async
	public static String healthCheck() throws IOException {
		return httpClient.doGet(URL_ + "/health/check");
	}

	@Async
	public static String register(AccountRegisterRequest accountRegisterRequest) throws IOException {
		return httpClient.doPost(URL_ + "/account/register", accountRegisterRequest);
	}
}
