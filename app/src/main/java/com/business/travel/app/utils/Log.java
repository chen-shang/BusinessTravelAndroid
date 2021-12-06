package com.business.travel.app.utils;

import com.blankj.utilcode.util.LogUtils;

public class Log {
	public static void debug(final Object... contents) {
		LogUtils.d(contents);
	}

	public static void info(final Object... contents) {
		LogUtils.i(contents);
	}

	public static void error(final Object... contents) {
		LogUtils.e(contents);
	}
}
