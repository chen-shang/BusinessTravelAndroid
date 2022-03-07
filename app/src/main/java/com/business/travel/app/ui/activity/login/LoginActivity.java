package com.business.travel.app.ui.activity.login;

import android.os.Bundle;
import android.view.View;
import com.business.travel.app.databinding.ActivityLoginBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，也需要在App代码中调
		 * 用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
		 * UMConfigure.init调用中appkey和channel参数请置为null）。
		 */
		UMConfigure.init(this, "61eeb912e014255fcb04447f", "Umeng", UMConfigure.DEVICE_TYPE_PHONE, "");

		PlatformConfig.setWeixin("wxaae6cbabcbc0aa53", "004b5c1fdfffae940090d8ab4f8b3d3f");
	}

	public void goMasterActivity(View view) {

	}
}