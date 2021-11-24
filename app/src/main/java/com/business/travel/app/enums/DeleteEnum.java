package com.business.travel.app.enums;

public enum DeleteEnum {
	DELETE(0, "已删除"),
	NOT_DELETE(1, "未删除"),
	;
	private final int code;
	private final String msg;

	DeleteEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
