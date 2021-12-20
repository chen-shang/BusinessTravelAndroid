package com.business.travel.app.enums;

public enum DateTimeTagEnum {
	TobeDetermined(-1L, "待定"),
	;

	private final Long code;
	private final String msg;

	DateTimeTagEnum(Long code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public Long getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}
}
