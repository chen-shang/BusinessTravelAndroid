package com.business.travel.app.enums;

public enum WebTextTypeEnum {
	USER_AGREEMENT("用户协议", "https://gitee.com/chen-shang/business-travel-resource/raw/master/about/user_agreement.txt"),
	PRIVACY_POLICY("隐私政策", ""),
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
