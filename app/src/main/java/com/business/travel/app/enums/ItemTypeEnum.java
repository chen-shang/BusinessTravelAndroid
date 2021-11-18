package com.business.travel.app.enums;

/**
 * item 类型是消费项还是同行人项
 */
public enum ItemTypeEnum {
	CONSUMPTION("消费项"),
	ASSOCIATE("同行人项");
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
