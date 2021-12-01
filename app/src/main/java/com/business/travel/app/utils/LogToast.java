package com.business.travel.app.utils;

import android.view.Gravity;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.aop.Async;

/**
 * @author chenshang
 */
public class LogToast {
	public static void errorShow(String msg) {
		ToastUtils.make().setLeftIcon(R.drawable.ic_base_error).setGravity(Gravity.CENTER, 0, 0).show(msg);
	}

	public static void infoShow(String msg) {
		ToastUtils.make().setLeftIcon(R.drawable.ic_base_info).setGravity(Gravity.CENTER, 0, 0).show(msg);
	}
}
