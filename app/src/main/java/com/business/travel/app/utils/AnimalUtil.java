package com.business.travel.app.utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

/**
 * @author chenshang
 */
public class AnimalUtil {
	private static final float v = 180.0F;
	private static final long duration = 800;

	/**
	 * 旋转上升动画
	 *
	 * @param view
	 */
	public static void show(View view, Orientation orientation) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = null;
		if (Orientation.RIGHT_LEFT == orientation) {
			rotation = ObjectAnimator.ofFloat(view, "rotation", 0.0F, v);
		} else if (Orientation.LEFT_RIGHT == orientation) {
			rotation = ObjectAnimator.ofFloat(view, "rotation", v, 0.0F);
		}
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1f, 1.1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1f, 1.1f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", 0, -view.getHeight() * 0.45F);
		//动画时间
		animatorSet.setDuration(duration);
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
	public static void reset(View view, Orientation orientation) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = null;
		if (Orientation.RIGHT_LEFT == orientation) {
			rotation = ObjectAnimator.ofFloat(view, "rotation", 0.0F, v);
		} else if (Orientation.LEFT_RIGHT == orientation) {
			rotation = ObjectAnimator.ofFloat(view, "rotation", v, 0.0F);
		}
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(view, "translationY", -view.getHeight() * 0.45F, 0);

		//动画时间
		animatorSet.setDuration(duration);
		//设置插值器
		animatorSet.setInterpolator(new DecelerateInterpolator());
		//同时执行
		animatorSet.play(rotation).with(scaleX).with(scaleY).with(translationY);
		//启动动画
		animatorSet.start();
	}
}
