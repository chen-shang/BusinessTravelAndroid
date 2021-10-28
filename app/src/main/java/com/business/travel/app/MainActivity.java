package com.business.travel.app;

import java.util.Objects;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.activity.MasterActivity;
import com.business.travel.app.activity.TestActivity;
import com.business.travel.app.databinding.ActivityMainBinding;

/**
 * @author chenshang
 */
public class MainActivity extends AppCompatActivity {

	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		BarUtils.setStatusBarColor(this, ColorUtils.getColor(R.color.white));
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		binding.textViewSignUp.setOnClickListener(v -> {
			Intent intent = new Intent(this, TestActivity.class);
			startActivity(intent);
		});
	}

	public void goMasterActivity(View view) {
		Intent intent = new Intent(this, MasterActivity.class);
		startActivity(intent);
	}

	public void goAccountLongActivity(View view) {}

	public void goSingUpActivity(View view) {

	}
}