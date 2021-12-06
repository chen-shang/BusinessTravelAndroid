package com.business.travel.app.ui;

import android.os.Bundle;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import lombok.SneakyThrows;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {
	String iconDownloadUrl = "https://gitee.com/chen-shang/business-travel-resource/raw/master/icon/CONSUMPTION/4-%E6%9C%8D%E9%A5%B0/10banxie.svg";

	@Override
	protected void inject() {
	}

	@SneakyThrows
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();

	}
}