package com.business.travel.app;

import java.util.Objects;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.ColorUtils;

/**
 * @author chenshang
 */
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		BarUtils.setStatusBarColor(this, ColorUtils.getColor(R.color.white));
		setContentView(R.layout.activity_main);
	}

	public void goMasterActivity(View view) {}

	public void goAccountLongActivity(View view) {}

	public void goSingUpActivity(View view) {

	}
}