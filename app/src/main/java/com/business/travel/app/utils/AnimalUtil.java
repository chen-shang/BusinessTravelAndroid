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
    /**
     * 旋转角度
     */
    private static final float rotation = 180.0F;
    /**
     * 持续事件
     */
    private static final long duration = 800;
    /**
     * 开始大小
     */
    private static final float startSize = 0.6f;

    /**
     * 结束大小
     */
    private static final float endSize = 1f;

    /**
     * 旋转上升动画
     *
     * @param floatingActionButton
     */
    public static void show(FloatingActionButton floatingActionButton, Orientation orientation) {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator rotation = genRotation(floatingActionButton, orientation);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", startSize, endSize);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", startSize, endSize);
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
                floatingActionButton.setImageResource(R.drawable.ic_base_bill_add);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
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
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(floatingActionButton, "scaleX", endSize, startSize);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(floatingActionButton, "scaleY", endSize, startSize);
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
                floatingActionButton.setImageResource(R.drawable.ic_base_bill_add_gray);
            }
        });
        //启动动画
        animatorSet.start();
    }

    /**
     * 计算旋转方向
     *
     * @param floatingActionButton
     * @param orientation
     * @return
     */
    private static ObjectAnimator genRotation(FloatingActionButton floatingActionButton, Orientation orientation) {
        if (Orientation.RIGHT_LEFT == orientation) {
            return ObjectAnimator.ofFloat(floatingActionButton, "rotation", 0.0F, rotation);
        } else if (Orientation.LEFT_RIGHT == orientation) {
            return ObjectAnimator.ofFloat(floatingActionButton, "rotation", rotation, 0.0F);
        }
        return ObjectAnimator.ofFloat(floatingActionButton, "rotation", 0.0F, rotation);
    }
}
