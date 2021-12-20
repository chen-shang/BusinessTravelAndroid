package com.business.travel.app.utils;

import android.content.res.Resources;
import android.os.Looper;
import android.os.MessageQueue;
import android.view.View;
import android.view.Window;
import androidx.annotation.DrawableRes;

public class StatusBarUtil {

	/**
	 * 设置状态栏（渐变）
	 */
	public static void setStatusBarView(Window window, Resources resources, @DrawableRes int resid) {
		//延时加载数据，保证Statusbar绘制完成后再findview。
		Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
			@Override
			public boolean queueIdle() {
				int identifier = resources.getIdentifier("statusBarBackground", "id", "android");
				View statusBarView = window.findViewById(identifier);
				statusBarView.setBackgroundResource(resid);

				//不加监听,也能实现改变statusbar颜色的效果。但是会出现问题：比如弹软键盘后,弹popwindow后,引起window状态改变时,statusbar的颜色就会复原.
				window.getDecorView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
					@Override
					public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
						statusBarView.setBackgroundResource(resid);

						window.getDecorView().removeOnLayoutChangeListener(this);
					}
				});

				//只走一次
				return false;
			}
		});
	}
}
