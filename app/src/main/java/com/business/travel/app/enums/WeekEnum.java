package com.business.travel.app.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public enum WeekEnum {
	MONDAY(1, "星期一"),
	TUESDAY(2, "星期二"),
	WEDNESDAY(3, "星期三"),
	THURSDAY(4, "星期四"),
	FRIDAY(5, "星期五"),
	SATURDAY(6, "星期六"),
	SUNDAY(7, "星期日");
	private static final Map<Integer, WeekEnum> maps = new HashMap<>();

	static {
		Arrays.stream(values()).forEach(item -> maps.put(item.getCode(), item));
	}

	private final int code;
	private final String msg;

	WeekEnum(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	public static WeekEnum ofCode(int code) {
		return maps.get(code);
	}

	public int getCode() {
		return code;
	}

	public String getMsg() {
		return msg;
	}

}
