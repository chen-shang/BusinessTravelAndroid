package com.business.travel.app.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * @author chenshang
 */
public class AnimalUtil {
	/**
	 * 旋转上升动画
	 *
	 * @param view
	 */
	public static void show(View view) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0.0F, 360.0F);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.2f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.2f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -view.getHeight() * 0.45F);
		//动画时间
		animatorSet.setDuration(1000);
		//设置插值器
		animatorSet.setInterpolator(new DecelerateInterpolator());
		//同时执行
		animatorSet.play(rotation).with(scaleX).with(scaleY).with(translationY);
		//启动动画
		animatorSet.start();
	}

	/**
	 * 旋转复位动画
	 *
	 * @param view
	 */
	public static void reset(View view) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotation", 0.0F, 360.0F);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.2f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.2f, 1f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight() * 0.45F, 0);

		//动画时间
		animatorSet.setDuration(1000);
		//设置插值器
		animatorSet.setInterpolator(new DecelerateInterpolator());
		//同时执行
		animatorSet.play(rotation).with(scaleX).with(scaleY).with(translationY);
		//启动动画
		animatorSet.start();
	}
}
