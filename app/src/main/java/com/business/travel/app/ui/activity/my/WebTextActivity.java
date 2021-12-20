package com.business.travel.app.ui.activity.my;

import android.os.Bundle;
import com.business.travel.app.databinding.ActivityWebTextBinding;
import com.business.travel.app.enums.WebTextTypeEnum;
import com.business.travel.app.ui.base.ColorStatusBarActivity;

public class WebTextActivity extends ColorStatusBarActivity<ActivityWebTextBinding> {

	private String webTextType;

	@Override
	protected void inject() {
		webTextType = this.getIntent().getStringExtra("WebTextType");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WebTextTypeEnum webTextTypeEnum = WebTextTypeEnum.valueOf(webTextType);
		viewBinding.topTitleBar.contentBarTitle.setText(webTextTypeEnum.getMsg());
		viewBinding.webContent.loadUrl(webTextTypeEnum.getUrl());
	}
}