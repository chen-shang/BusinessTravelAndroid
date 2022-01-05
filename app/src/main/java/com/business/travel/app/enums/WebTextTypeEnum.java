package com.business.travel.app.enums;

import com.business.travel.app.constant.BusinessTravelResourceConstant;

/**
 * 页面类型和页面文本地址
 */
public enum WebTextTypeEnum {
	USER_AGREEMENT("用户协议", BusinessTravelResourceConstant.USER_AGREEMENT_PATH),
	PRIVACY_POLICY("隐私政策", BusinessTravelResourceConstant.PRIVACY_POLICY_PATH),
	;
	private final String msg;
	private final String url;

	WebTextTypeEnum(String msg, String url) {
		this.msg = msg;
		this.url = url;
	}

	public String getMsg() {
		return msg;
	}

	public String getUrl() {
		return url;
	}
}
