package com.business.travel.app.utils;

import android.view.Gravity;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.aop.Async;

/**
 * @author chenshang
 */
public class LogToast {
	@Async
	public static void errorShow(String msg) {
		ToastUtils.make().setLeftIcon(R.drawable.icon_error).setGravity(Gravity.CENTER, 0, 0).show(msg);
	}

	@Async
	public static void infoShow(String msg) {
		ToastUtils.make().setLeftIcon(R.drawable.icon_info).setGravity(Gravity.CENTER, 0, 0).show(msg);
	}
}
