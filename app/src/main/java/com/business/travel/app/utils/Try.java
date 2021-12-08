package com.business.travel.app.utils;

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

}
