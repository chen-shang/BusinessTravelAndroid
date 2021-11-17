package com.business.travel.app.enums;

/**
 * 消费类型
 */
public enum ConsumptionItemTypeEnum {
	INCOME(0, "收入"),
	SPENDING(1, "支出"),
	;

	private final int code;
	private final String msg;

	ConsumptionItemTypeEnum(int code, String msg) {
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
