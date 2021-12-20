package com.business.travel.app.ui.activity.my;

import android.content.Intent;
import android.os.Bundle;
import com.business.travel.app.databinding.ActivityAboutMeBinding;
import com.business.travel.app.enums.WebTextTypeEnum;
import com.business.travel.app.ui.base.ColorStatusBarActivity;

public class AboutMeActivity extends ColorStatusBarActivity<ActivityAboutMeBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//注册用户协议点击事件
		viewBinding.aboutMeUserAgreement.setOnClickListener(v -> {
			Intent intent = new Intent(this, WebTextActivity.class);
			intent.putExtra("WebTextType", WebTextTypeEnum.USER_AGREEMENT.name());
			startActivity(intent);
		});

		//注册隐私政策
		viewBinding.aboutMePolicy.setOnClickListener(v -> {
			Intent intent = new Intent(this, WebTextActivity.class);
			intent.putExtra("WebTextType", WebTextTypeEnum.PRIVACY_POLICY.name());
			startActivity(intent);
		});
	}
}