package com.business.travel.app.utils;

import java.util.function.Supplier;

import com.blankj.utilcode.util.LogUtils;

public class Try {

	public static void of(Runnable runnable) {
		try {
			runnable.run();
		} catch (Exception e) {
			LogUtils.e(e);
			LogToast.errorShow(e.getMessage());
		}
	}

	public static <T> T of(Supplier<T> supplier) {
		try {
			return supplier.get();
		} catch (Exception e) {
			LogUtils.e(e);
			LogToast.errorShow(e.getMessage());
		}
		return null;
	}

}
