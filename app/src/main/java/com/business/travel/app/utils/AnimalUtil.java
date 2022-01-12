package com.business.travel.app.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.animation.DecelerateInterpolator;
import com.business.travel.app.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class AnimalUtil {
	private static final float v = 180.0F;
	private static final long duration = 800;

	/**
	 * 旋转上升动画
	 *
	 * @param floatingActionButton
	 */
	public static void show(FloatingActionButton floatingActionButton, Orientation orientation) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = genRotation(floatingActionButton, orientation);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", 0.8f, 1.1f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", 0.8f, 1.1f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(floatingActionButton, "translationY", 0, -floatingActionButton.getHeight() * 0.45F);
		//动画时间
		animatorSet.setDuration(duration);
		//设置插值器
		animatorSet.setInterpolator(new DecelerateInterpolator());
		//同时执行
		animatorSet.play(rotation).with(scaleX).with(scaleY).with(translationY);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationStart(Animator animation) {
				super.onAnimationResume(animation);
				floatingActionButton.setImageResource(R.drawable.ic_base_bill_add);
			}
		});
		//启动动画
		animatorSet.start();
	}

	/**
	 * 旋转复位动画
	 *
	 * @param floatingActionButton
	 */
	public static void reset(FloatingActionButton floatingActionButton, Orientation orientation) {
		AnimatorSet animatorSet = new AnimatorSet();
		ObjectAnimator rotation = genRotation(floatingActionButton, orientation);
		ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", 1.1f, 0.8f);
		ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", 1.1f, 0.8f);
		ObjectAnimator translationY = ObjectAnimator.ofFloat(floatingActionButton, "translationY", -floatingActionButton.getHeight() * 0.45F, 0);
		//动画时间
		animatorSet.setDuration(duration);
		//设置插值器
		animatorSet.setInterpolator(new DecelerateInterpolator());
		//同时执行
		animatorSet.play(rotation).with(scaleX).with(scaleY).with(translationY);
		animatorSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);
				floatingActionButton.setImageResource(R.drawable.ic_base_bill_add_gray);
			}
		});
		//启动动画
		animatorSet.start();
	}

	private static ObjectAnimator genRotation(FloatingActionButton floatingActionButton, Orientation orientation) {
		if (Orientation.RIGHT_LEFT == orientation) {
			return ObjectAnimator.ofFloat(floatingActionButton, "rotation", 0.0F, v);
		} else if (Orientation.LEFT_RIGHT == orientation) {
			return ObjectAnimator.ofFloat(floatingActionButton, "rotation", v, 0.0F);
		}
		return ObjectAnimator.ofFloat(floatingActionButton, "rotation", 0.0F, v);
	}
}
