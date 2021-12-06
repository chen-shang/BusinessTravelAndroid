package com.business.travel.app.ui.activity.version;

import android.os.Bundle;
import com.business.travel.app.databinding.ActivityVersionHistoryBinding;
import com.business.travel.app.ui.base.BaseActivity;

public class VersionHistoryActivity extends BaseActivity<ActivityVersionHistoryBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String text = "1. 两次滑动返回主界面\n" + "2.图标改变自身颜\n" + "3.账单页面样式" +
				"4. 账单显示星期几\n" +"5. 我的页面";
		viewBinding.versionHistory.setText(text);
	}
}