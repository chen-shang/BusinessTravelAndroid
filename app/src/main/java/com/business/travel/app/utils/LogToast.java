package com.business.travel.app.utils;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;

/**
 * @author chenshang
 */
public class LogToast {
    public static void errorShow(String msg) {
        LogUtils.e(msg);
        ToastUtils.make().setLeftIcon(R.drawable.ic_base_error).show(msg);
    }

    public static void infoShow(String msg) {
        LogUtils.i(msg);
        ToastUtils.make().setLeftIcon(R.drawable.ic_base_info).show(msg);
    }
}
