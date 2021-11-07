package com.business.travel.app;

import java.util.Objects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.databinding.ActivityMainBinding;
import com.business.travel.app.ui.MasterActivity;
import com.business.travel.app.ui.TestActivity;
import com.business.travel.app.ui.base.BaseActivity;

/**
 * @author chenshang
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		BarUtils.setStatusBarColor(this, ColorUtils.getColor(R.color.white));
		viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(viewBinding.getRoot());

		viewBinding.UIMainActivityImageViewIcon.setOnClickListener(v -> {
			Intent intent = new Intent(this, TestActivity.class);
			startActivity(intent);
		});
	}

	public void goMasterActivity(View view) {
		Intent intent = new Intent(this, MasterActivity.class);
		startActivity(intent);
	}

	public void goAccountLongActivity(View view) {
	}

	public void goSingUpActivity(View view) {

	}
}