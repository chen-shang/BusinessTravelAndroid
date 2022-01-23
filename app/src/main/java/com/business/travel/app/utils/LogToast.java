package com.business.travel.app.utils;

import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;

/**
 * @author chenshang
 */
public class LogToast {
    public static void errorShow(String msg) {
        ToastUtils.make().setLeftIcon(R.drawable.ic_base_error).show(msg);
    }

    public static void infoShow(String msg) {
        ToastUtils.make().setLeftIcon(R.drawable.ic_base_info).show(msg);
    }
}
