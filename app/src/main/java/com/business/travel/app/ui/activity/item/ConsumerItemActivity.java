package com.business.travel.app.ui.activity.item;

import java.util.Objects;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityConsumerItemBinding;
import com.business.travel.app.ui.base.BaseActivity;

/**
 * @author chenshang
 */
public class ConsumerItemActivity extends BaseActivity<ActivityConsumerItemBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		GradientDrawable gradientDrawableLeft = (GradientDrawable)viewBinding.left.getBackground();
		GradientDrawable gradientDrawableRight = (GradientDrawable)viewBinding.right.getBackground();

		viewBinding.left.setTextColor(ColorUtils.getColor(R.color.teal_800));
		gradientDrawableLeft.setColor(ColorUtils.getColor(R.color.white));

		viewBinding.right.setTextColor(ColorUtils.getColor(R.color.white));
		gradientDrawableRight.setColor(ColorUtils.getColor(R.color.teal_800));

		viewBinding.left.setOnClickListener(v -> {
			viewBinding.left.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableLeft.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.right.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableRight.setColor(ColorUtils.getColor(R.color.teal_800));


		});

		viewBinding.right.setOnClickListener(v -> {
			viewBinding.right.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableRight.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.left.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableLeft.setColor(ColorUtils.getColor(R.color.teal_800));
		});



	}
}