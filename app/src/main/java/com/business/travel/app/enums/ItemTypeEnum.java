package com.business.travel.app.enums;

/**
 * item 类型是消费项还是人员
 */
public enum ItemTypeEnum {
	CONSUMPTION("消费项"),
	MEMBER("人员");
	/**
	 * 枚举描述
	 */
	private final String msg;

	ItemTypeEnum(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
}
